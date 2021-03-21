package gartham.c10ver.processing.trading;

import java.math.BigInteger;
import java.util.Locale;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

class AddCommand extends MatchBasedCommand {

	private final Trade trade;

	public AddCommand(Trade trade) {
		super("+");
		this.trade = trade;
	}

	@Override
	public void exec(CommandInvocation inv) {
		var ecouser = trade.getManager().getClover().getEconomy().getUser(inv.event.getAuthor().getId());

		Inventory.Entry<?> e;
		ENTRY_FINDER: {
			try {
				int x = Integer.parseInt(inv.args[0]) - 1;
				if (ecouser.getInventory().getEntryCount() < x) {
					e = ecouser.getInventory().get(x);
					break ENTRY_FINDER;
				} else {
					trade.getInitialChannel().sendMessage(inv.event.getAuthor().getAsMention() + ", you only have `"
							+ ecouser.getInventory().getEntryCount() + "` items! Please use a number between `1` and `"
							+ ecouser.getInventory().getEntryCount() + "` or use the item's ID.").queue();
					return;
				}
			} catch (NumberFormatException e1) {
			}
			if ((e = ecouser.getInventory().get(inv.args[0])) == null)
				if ((e = ecouser.getInventory().get(inv.args[0].toLowerCase(Locale.ENGLISH))) == null) {
					inv.event.getChannel()
							.sendMessage(
									inv.event.getAuthor().getAsMention() + ", you don't have any items with that ID.")
							.queue();
					return;
				}
		}

		BigInteger amt;
		final Entry<?>.ItemStack i;

		if (inv.args.length == 0) {// +loot-crate
			amt = BigInteger.ONE;
			i = e.get(0);
		} else if (inv.args.length == 2)// +item 7
			if (e.getStacks().size() == 1) {// +pizza 3
				try {
					amt = new BigInteger(inv.args[1]);
				} catch (NumberFormatException e2) {// If item does not have more stacks.
					int amount = (int) (Math.random() * 10) + 1;
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + " please provide an amount: `"
									+ Utilities.strip(inv.cmdName + inv.args[0] + ' ' + amount) + "` for `" + amount
									+ "` items.")
							.queue();
					return;
				}

				if (amt.compareTo(BigInteger.ONE) < 0) {// If amt < 1
					inv.event.getChannel().sendMessage(
							inv.event.getAuthor().getAsMention() + " you can't add less than 1 items to a trade...")
							.queue();
					return;
				}

				i = e.get(0);
			} else {// +loot-crate 2
				int x;
				try {
					x = Integer.parseInt(inv.args[1]);
				} catch (NumberFormatException e2) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention()
									+ " please specify which type of that item you want to trade. For example, `+"
									+ e.getType() + " 1` to add some of the first type of that item, or: `+"
									+ e.getType() + " 2` to add some of the second.")
							.queue();
					return;
				}
				if (x > e.getStacks().size()) {
					inv.event.getChannel()
							.sendMessage(
									inv.event.getAuthor().getAsMention() + " that's not a valid index! You only have `"
											+ e.getStacks().size() + "` different types of that item.")
							.queue();
					return;
				}
				if (x < 1) {
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " that's not a valid index! Indices must be greater than 0.");
					return;
				}

				i = e.get(x - 1);
				amt = BigInteger.ONE;
			}
		else if (inv.args.length == 3) { // + item 3 9
			// For two numbers, the index is always first.
			int x;
			try {
				x = Integer.parseInt(inv.args[1]);
			} catch (NumberFormatException e1) {
				inv.event.getChannel()
						.sendMessage(inv.event.getAuthor().getAsMention() + " the index has to be a number.").queue();
				return;
			}
			if (x > e.getStacks().size()) {
				inv.event.getChannel()
						.sendMessage(inv.event.getAuthor().getAsMention() + " that's not a valid index! You only have `"
								+ e.getStacks().size() + "` different types of that item.")
						.queue();
				return;
			}
			if (x < 1) {
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
						+ " that's not a valid index! Indices must be greater than 0.").queue();
				return;
			}
			i = e.get(x - 1);

			try {
				amt = new BigInteger(inv.args[2]);
			} catch (NumberFormatException e2) {// If item does not have more stacks.
				int amount = (int) (Math.random() * 10) + 1;
				inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " please provide an amount: `"
						+ Utilities.strip(inv.cmdName + inv.args[0] + ' ' + amount) + "` for `" + amount + "` items.")
						.queue();
				return;
			}

			if (amt.compareTo(BigInteger.ONE) < 0) {// If amt < 1
				inv.event.getChannel()
						.sendMessage(
								inv.event.getAuthor().getAsMention() + " you can't add less than 1 items to a trade...")
						.queue();
				return;
			}

		} else {
			inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
					+ " too many arguments. Please provide only an amount and an index.").queue();
			return;
		}

		var person = trade.isRecipient(inv.event.getAuthor()) ? trade.getRecip() : trade.getRequester();
		BigInteger total = amt;
		if (person.getItems().get((Item) i.getItem()) != null)
			total = amt.add(person.getItems().get((Item) i.getItem()).getCount());

		if (total.compareTo(i.count()) > 0) {
			inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
					+ " you don't have enough items to add that many to the trade.").queue();
			return;
		}

		person.getItems().add((Item) i.getItem(), amt);
		inv.event.getChannel()
				.sendMessage(inv.event.getAuthor().getAsMention() + " added `" + amt
						+ "` of that item to the trade! Here's what you've listed so far: ")
				.embed(person.getTrade(new EmbedBuilder()).build()).queue();

	}

}
