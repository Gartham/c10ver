package gartham.c10ver.processing.commands;

import java.util.List;

import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public class InventoryCommand extends MatchBasedCommand {

	private final Clover clover;

	public InventoryCommand(Clover clover, String... aliases) {
		super(aliases);
		this.clover = clover;
	}

	public InventoryCommand(Matching matching, Clover clover) {
		super(matching);
		this.clover = clover;
	}

	@Override
	public void exec(CommandInvocation inv) {

		final Inventory invent;
		final String type;
		int page;
		ENTRIES: {
			if (inv.args.length == 1) {
				// The argument should be either an item type or a page.
				try {
					page = Integer.parseInt(inv.args[0]);
				} catch (NumberFormatException e) {
					invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
					type = inv.args[0];
					page = 1;
					break ENTRIES;
				}
				if (page < 1) {
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " `"
							+ Utilities.strip(inv.args[0]) + "` is not a valid page.").queue();
					return;
				}
			} else if (inv.args.length == 0)
				page = 1;
			else if (inv.args.length == 2) {
				PARSE_PAGE: {
					try {
						page = Integer.parseInt(inv.args[1]);
						if (page > 0)
							break PARSE_PAGE;
					} catch (NumberFormatException e) {
					}
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " `"
							+ Utilities.strip(inv.args[1]) + "` is not a valid page.").queue();
					return;
				}

				type = inv.args[0];
				invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
				break ENTRIES;
			} else {
				inv.event.getChannel()
						.sendMessage(inv.event.getAuthor().getAsMention() + " that command doesn't accept "
								+ inv.args.length + " arguments." + (inv.args.length > 10 ? " >:(" : ""))
						.queue();
				return;
			}

			invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
			List<Entry<?>> pageItems = invent.getPage(page, 9);
			int maxPage = invent.maxPage(9);
			if (pageItems == null) {
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " you only have `" + maxPage
						+ (maxPage == 1 ? "` page" : "` pages") + " in your inventory!").queue();
			} else {
				EmbedBuilder eb = new EmbedBuilder();

				eb.setAuthor(inv.event.getAuthor().getAsTag() + "'s Inventory", null,
						inv.event.getAuthor().getEffectiveAvatarUrl());
				eb.setDescription('*' + inv.event.getAuthor().getAsMention() + " has `" + invent.getEntryCount() + "` "
						+ (invent.getEntryCount() == 1 ? "type of item" : "different types of items") + " and `"
						+ invent.getTotalItemCount() + "` total items.*\n\u200B");
				printEntries(pageItems, eb);
				eb.addField("", "You have **" + maxPage + "** page" + (maxPage == 1 ? "" : "s") + " in your inventory.",
						false);
//				eb.setFooter(
//						"You have " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " in your inventory.");
				inv.event.getChannel().sendMessage(eb.build()).queue();
			}
			return;
		}

		Entry<?> entry = invent.get(type);
		if (entry == null)
			inv.event.getChannel()
					.sendMessage(inv.event.getAuthor().getAsMention() + " you don't have any items of that type!")
					.queue();
		else {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(inv.event.getAuthor().getAsTag() + "'s Inventory: " + entry.getName(), null,
					inv.event.getAuthor().getEffectiveAvatarUrl());
			eb.setDescription('*' + inv.event.getAuthor().getAsMention() + " has `" + entry.getTotalCount()
					+ "` of this item.*\n\u200B");
			int maxPage = Utilities.maxPage(9, entry.getStacks());
			if (page > maxPage) {
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " you only have `" + maxPage
						+ (maxPage == 1 ? "` page" : "` pages") + " of that item in your inventory!").queue();
				return;
			}
			List<? extends Entry<?>.ItemStack> list = Utilities.paginate(page, 9, entry.getStacks());
			printStacks(list, eb);
			eb.addField("", "You have **" + maxPage + "** page" + (maxPage == 1 ? "" : "s")
					+ " of this item in your inventory.", false);
//			eb.setFooter("You have " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " in your inventory.");
			inv.event.getChannel().sendMessage(eb.build()).queue();
		}
	}

	private final static EmbedBuilder printEntries(List<Entry<?>> entries, EmbedBuilder builder) {
		for (Entry<?> e : entries)
			builder.addField(e.getIcon() + ' ' + e.getName(),
					" *You have [`" + e.getTotalCount() + "`](https://clover.gartham.com 'Item ID: " + e.getType()
							+ ". Use the ID to get or interact with the item.') of this.*\nUse `inv " + e.getType()
							+ "` to see more.",
					true);
		return builder;
	}

	private final static EmbedBuilder printStacks(List<? extends Entry<?>.ItemStack> list, EmbedBuilder builder) {
		for (Entry<?>.ItemStack i : list) {
			StringBuilder sb = new StringBuilder();
			sb.append("*You have [`" + i.getCount() + "`](https://clover.gartham.com 'Item ID: " + i.getType()
					+ ". Use the ID to get or interact with the item.') of this.*\n");
			for (java.util.Map.Entry<String, PropertyObject.Property<?>> e : i.getItem().getPropertyMapView()
					.entrySet())
				if (e.getValue().isAttribute())
					sb.append(e.getKey() + ": `" + e.getValue().get() + "`\n");
			builder.addField(i.getIcon() + ' ' + i.getEffectiveName(), sb.toString(), true);
		}
		return builder;
	}
}
