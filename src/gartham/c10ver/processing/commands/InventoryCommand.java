package gartham.c10ver.processing.commands;

import java.awt.Color;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.economy.items.ItemCategory;
import gartham.c10ver.economy.items.UserInventory;
import gartham.c10ver.response.menus.ButtonPaginator;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.Button;

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

	private void displayCategory(ItemCategory category, CommandInvocation inv, Message message) {
		UserInventory inventory = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
		StringBuilder sb = new StringBuilder();
		sb.append(inv.event.getAuthor().getAsMention()).append("'s ").append(category.getIcon()).append(' ')
				.append(category.getDisplayName()).append(" items:\n\n");
		for (var e : inventory)
			for (var is : e)
				if (is.getItem().getCategory() == category)
					sb.append('`').append(Utilities.formatNumber(is.getCount())).append("`x ").append(is.getIcon())
							.append(' ').append(is.getEffectiveName()).append('\n');
		message.editMessageEmbeds().content(sb.toString()).setActionRows().queue();
	}

	private void displayRoot(CommandInvocation inv) {
		EmbedBuilder eb = new EmbedBuilder();
		UserInventory inventory = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
		if (inventory.isEmpty()) {
			eb.appendDescription("Your inventory is empty!");
			eb.setColor(new Color(0xc20000));
			inv.event.getChannel().sendMessageEmbeds(eb.build()).queue();
		} else {
			Map<ItemCategory, BigInteger> ics = new HashMap<>();
			for (var e : inventory)
				for (var is : e)
					if (ics.containsKey(is.getItem().getCategory()))
						ics.put(is.getItem().getCategory(), ics.get(is.getItem().getCategory()).add(is.count()));
					else
						ics.put(is.getItem().getCategory(), is.count());

			eb.appendDescription(
					"You have **" + Utilities.formatNumber(inventory.getTotalItemCount()) + "** items.\n\n");

			eb.setTitle(inv.event.getAuthor().getAsTag() + "'s Inventory")
					.setAuthor(
							inv.event.isFromGuild() ? inv.event.getMember().getEffectiveName()
									: inv.event.getAuthor().getName(),
							null, inv.event.getAuthor().getEffectiveAvatarUrl());
			eb.appendDescription("Page **1** of **1**.");

			ButtonPaginator bp = new ButtonPaginator(clover.getEventHandler().getButtonClickProcessor());

			bp.setTarget(inv.event.getAuthor());

			bp.getMah().new Action(Button.secondary("sel", "\u200b").asDisabled()).reposition(2);
			bp.setHandler(t -> {
				var cat = ItemCategory.valueOf(t.getComponentId());
				displayCategory(cat, inv, bp.getMsg());
				return false;// return whether we updated the event.
			});
			bp.setMaxPage(0);
			bp.setOneTime(true);

			for (var i : ics.entrySet()) {
				eb.addField(i.getKey().getDisplayName() + " " + i.getKey().getIcon(),
						"Items: " + (i.getValue().equals(BigInteger.ZERO) ? "None"
								: "**" + Utilities.formatNumber(i.getValue()) + "**"),
						true);
				bp.getMah().new Action(Button.success(i.getKey().name(), Emoji.fromMarkdown(i.getKey().getIcon()))
						.withLabel(i.getKey().getDisplayName()));
			}

			bp.attachAndSend(inv.event.getChannel().sendMessageEmbeds(eb.build()));
		}

	}

	@Override
	public void exec(CommandInvocation inv) {

		displayRoot(inv);

//		final UserInventory invent;
//		final String type;
//		int page;
//		ENTRIES: {
//			if (inv.args.length == 1) {
//				// The argument should be either an item type or a page.
//				try {
//					page = Integer.parseInt(inv.args[0]);
//				} catch (NumberFormatException e) {
//					invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
//					type = inv.args[0];
//					page = 1;
//					break ENTRIES;
//				}
//				if (page < 1) {
//					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " `"
//							+ Utilities.strip(inv.args[0]) + "` is not a valid page.").queue();
//					return;
//				}
//			} else if (inv.args.length == 0)
//				page = 1;
//			else if (inv.args.length == 2) {
//				PARSE_PAGE: {
//					try {
//						page = Integer.parseInt(inv.args[1]);
//						if (page > 0)
//							break PARSE_PAGE;
//					} catch (NumberFormatException e) {
//					}
//					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " `"
//							+ Utilities.strip(inv.args[1]) + "` is not a valid page.").queue();
//					return;
//				}
//
//				type = inv.args[0];
//				invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
//				break ENTRIES;
//			} else {
//				inv.event.getChannel()
//						.sendMessage(inv.event.getAuthor().getAsMention() + " that command doesn't accept "
//								+ inv.args.length + " arguments." + (inv.args.length > 10 ? " >:(" : ""))
//						.queue();
//				return;
//			}
//
//			invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
//			var pageItems = invent.getPage(page, 9);
//			int maxPage = invent.maxPage(9);
//			if (pageItems == null) {
//				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " you only have `" + maxPage
//						+ (maxPage == 1 ? "` page" : "` pages") + " in your inventory!").queue();
//			} else {
//				EmbedBuilder eb = new EmbedBuilder();
//
//				eb.setAuthor(inv.event.getAuthor().getAsTag() + "'s Inventory", null,
//						inv.event.getAuthor().getEffectiveAvatarUrl());
//				eb.setDescription('*' + inv.event.getAuthor().getAsMention() + " has `" + invent.getEntryCount() + "` "
//						+ (invent.getEntryCount() == 1 ? "type of item" : "different types of items") + " and `"
//						+ invent.getTotalItemCount() + "` total items.*\n\u200B");
//				printEntries(pageItems, eb);
//				eb.addField("", "You have **" + maxPage + "** page" + (maxPage == 1 ? "" : "s") + " in your inventory.",
//						false);
////				eb.setFooter(
////						"You have " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " in your inventory.");
//				inv.event.getChannel().sendMessageEmbeds(eb.build()).queue();
//			}
//			return;
//		}
//
//		var entry = invent.get(type);
//		if (entry == null)
//			inv.event.getChannel()
//					.sendMessage(inv.event.getAuthor().getAsMention() + " you don't have any items of that type!")
//					.queue();
//		else {
//			EmbedBuilder eb = new EmbedBuilder();
//			eb.setAuthor(inv.event.getAuthor().getAsTag() + "'s Inventory: " + entry.getName(), null,
//					inv.event.getAuthor().getEffectiveAvatarUrl());
//			eb.setDescription('*' + inv.event.getAuthor().getAsMention() + " has `" + entry.getTotalCount()
//					+ "` of this item.*\n\u200B");
//			int maxPage = JavaTools.maxPage(9, entry.getStacks());
//			if (page > maxPage) {
//				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " you only have `" + maxPage
//						+ (maxPage == 1 ? "` page" : "` pages") + " of that item in your inventory!").queue();
//				return;
//			}
//			List<? extends Entry<?>.ItemStack> list = JavaTools.paginate(page, 9, entry.getStacks());
//			printStacks(list, eb);
//			eb.addField("", "You have **" + maxPage + "** page" + (maxPage == 1 ? "" : "s")
//					+ " of this item in your inventory.", false);
////			eb.setFooter("You have " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " in your inventory.");
//			inv.event.getChannel().sendMessageEmbeds(eb.build()).queue();
//		}
	}

	private final static EmbedBuilder printEntries(List<? extends Entry<?>> entries, EmbedBuilder builder) {
		for (var e : entries)
			builder.addField(e.getIcon() + ' ' + e.getName(),
					" *You have [`" + e.getTotalCount() + "`](https://clover.gartham.com 'Item ID: " + e.getType()
							+ ". Use the ID to get or interact with the item.') of this.*\nUse `inv " + e.getType()
							+ "` to see more.",
					true);
		return builder;
	}

	private final static EmbedBuilder printStacks(List<? extends Entry<?>.ItemStack> list, EmbedBuilder builder) {
		for (var i : list) {
			StringBuilder sb = new StringBuilder();
			sb.append("*You have [`" + i.getCount() + "`](https://clover.gartham.com 'Item ID: " + i.getType()
					+ ". Use the ID to get or interact with the item.') of this.*\n");
			for (java.util.Map.Entry<String, PropertyObject.Property<?>> e : i.getItem().getPropertyMapView()
					.entrySet())
				if (e.getValue().isAttribute())
					sb.append(i.getItem().userFriendlyName(e.getKey()) + ": `"
							+ i.getItem().userFriendlyValue(e.getKey()) + "`\n");
			builder.addField(i.getIcon() + ' ' + i.getEffectiveName(), sb.toString(), true);
		}
		return builder;
	}
}
