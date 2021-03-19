package gartham.c10ver.processing.trading;

import java.math.BigInteger;
import java.util.Locale;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.economy.items.Item;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

class AddCommand extends MatchBasedCommand {

	private final Trade trade;

	public AddCommand(Trade trade) {
		super("+");
		this.trade = trade;
	}

	private BigInteger parseAmount(String arg, MessageReceivedEvent event) {
		BigInteger amt;
		try {
			amt = new BigInteger(arg);
		} catch (NumberFormatException e2) {
			event.getChannel()
					.sendMessage(event.getAuthor().getAsMention() + " the amount you specify has to be a number.")
					.queue();
			return null;
		}
		if (amt.compareTo(BigInteger.ZERO) <= 0) {
			event.getChannel()
					.sendMessage(event.getAuthor().getAsMention() + " the amount you specify has to be at least 1.")
					.queue();
			return null;
		}

		return amt;
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

		if (e.getStacks().size() == 1) {

		} else {
			// If there is more than one type of stack in this entry, users are required to
			// specify the index of the item they want to add, or simply include "all" or
			// "*" as the index.
		}

		if (inv.args.length == 0) {
			amt = BigInteger.ONE;
			i = e.get(0);
		} else if (inv.args.length < 3) {
			if (inv.args.length == 1) {
				if (e.getStacks().size() == 1) {
					amt = parseAmount(inv.args[0], inv.event);
					i = e.get(0);
				} else {
					amt = BigInteger.ONE;
					int x;
					try {
						x = Integer.parseInt(inv.args[0]);
					} catch (NumberFormatException e1) {
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + " the index has to be a number.")
								.queue();
						return;
					}
					if (x > e.getStacks().size()) {
						inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
								+ " you don't have any items at that index for that category.").queue();
						return;
					}
					i = e.get(x - 1);
				}
			} else {
				amt = parseAmount(inv.args[1], inv.event);
				if (amt == null)
					return;
				int x;
				try {
					x = Integer.parseInt(inv.args[0]);
				} catch (NumberFormatException e1) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + " the index has to be a number.")
							.queue();
					return;
				}
				if (x > e.getStacks().size()) {
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " you don't have any items at that index for that category.").queue();
					return;
				}
				i = e.get(x - 1);
			}
		} else {
			inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
					+ " too many arguments. Please provide only an amount and an index.").queue();
			return;
		}

		var person = trade.isRecipient(inv.event.getAuthor()) ? trade.getRecip() : trade.getRequester();
		var inventry = person.getItems().get((Item) i.getItem());

		BigInteger alrd = inventry == null ? BigInteger.ZERO : inventry.getCount();

		if (alrd.add(amt).compareTo(i.count()) > 0)
			amt = i.count().subtract(alrd);

		if (amt.equals(BigInteger.ZERO)) {
			inv.event.getChannel().sendMessage(
					inv.event.getAuthor().getAsMention() + " **you've already added** all of that item that you have.")
					.queue();
		} else {
			person.getItems().add((Item) i.getItem(), amt);
			inv.event.getChannel()
					.sendMessage(inv.event.getAuthor().getAsMention() + " added `" + amt
							+ "` of that item to the trade! Here's what you've listed so far: ")
					.embed(person.getTrade(new EmbedBuilder()).build()).queue();
		}

	}

}
