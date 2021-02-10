package gartham.c10ver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.economy.Account;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.economy.items.LootCrateItem;
import gartham.c10ver.economy.items.LootCrateItem.CrateType;
import gartham.c10ver.users.User;
import gartham.c10ver.utils.FormattingUtils;
import net.dv8tion.jda.api.EmbedBuilder;

public class CloverCommandProcessor extends CommandProcessor {

	private final Clover clover;

	public CloverCommandProcessor(Clover clover) {
		this.clover = clover;
	}

	{
		register(new MatchBasedCommand("daily") {
			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();

				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastDaily().toDays() < 1)
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ FormattingUtils.formatLargest(Duration.ofDays(1).minus(u.timeSinceLastDaily()), 3)
									+ "` before running that command.")
							.queue();
				else {
					u.dailyInvoked();
					long amt = (long) (Math.random() * 25 + 10);
					u.getAccount().deposit(amt);
					inv.event.getChannel().sendMessage("You received `" + amt + "` garthcoins. You now have `"
							+ u.getAccount().getBalance().toPlainString() + "` garthcoins.").queue();
				}

			}
		});

		register(new MatchBasedCommand("weekly") {
			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastWeekly().toDays() < 7) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention()
									+ ", you must wait `" + FormattingUtils
											.formatLargest(Duration.ofDays(7).minus(u.timeSinceLastWeekly()), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.weeklyInvoked();
					long amt = (long) (Math.random() * 250 + 100);
					u.getAccount().deposit(amt);
					inv.event.getChannel().sendMessage("You received `" + amt + "` garthcoins. You now have `"
							+ u.getAccount().getBalance().toPlainString() + "` garthcoins.").queue();
				}
			}
		});

		register(new MatchBasedCommand("monthly") {
			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastWeekly().toDays() < 7) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention()
									+ ", you must wait `" + FormattingUtils
											.formatLargest(Duration.ofDays(30).minus(u.timeSinceLastWeekly()), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.weeklyInvoked();
					long amt = (long) (Math.random() * 10000 + 4000);
					u.getAccount().deposit(amt);
					inv.event.getChannel().sendMessage("You received `" + amt + "` garthcoins. You now have `"
							+ u.getAccount().getBalance().toPlainString() + "` garthcoins.").queue();
				}
			}
		});

		// TODO pay askdjflaskjhfd@Bob12987u1kmfdlskjflds 500
		register(new MatchBasedCommand("pay") {

			@Override
			public void exec(CommandInvocation inv) {
				if (inv.args.length != 2)
					inv.event.getChannel()
							.sendMessage("You need to specify two arguments, a user to pay, and an amount to pay.")
							.queue();
				else {
					BigInteger bi;
					try {
						bi = new BigInteger(inv.args[1]);
					} catch (NumberFormatException e) {
						inv.event.getChannel().sendMessage("Your second argument needs to be a number.").queue();
						return;
					}
					var mentionedUsers = inv.event.getMessage().getMentionedUsers();
					if (mentionedUsers.size() != 1) {
						inv.event.getChannel().sendMessage("You need to specify one user to pay money to.").queue();
						return;
					}

					if (inv.event.getAuthor().getId().equals(mentionedUsers.get(0).getId())) {
						inv.event.getChannel().sendMessage("You can't pay yourself money... :thinking:").queue();
						return;
					}

					Account payer = clover.getEconomy().getAccount(inv.event.getAuthor().getId()),
							recip = clover.getEconomy().getAccount(mentionedUsers.get(0).getId());

					if (payer.pay(new BigDecimal(bi), recip))
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + ", you paid `" + bi + "` to "
										+ mentionedUsers.get(0).getAsMention() + ". You now have `" + payer.getBalance()
										+ "`.")
								.queue();
					else
						inv.event.getChannel()
								.sendMessage(
										inv.event.getAuthor().getAsMention() + ", you do not have enough money to pay `"
												+ bi + "` to " + mentionedUsers.get(0).getAsMention() + '.')
								.queue();
				}
			}
		});

		register(new MatchBasedCommand("bal", "balance") {

			@Override
			public void exec(CommandInvocation inv) {
				inv.event.getChannel()
						.sendMessage(inv.event.getAuthor().getAsMention() + ", you have `"
								+ clover.getEconomy().getAccount(inv.event.getAuthor().getId()).getBalance() + "`.")
						.queue();
			}
		});

		register(new MatchBasedCommand("test") {

			@Override
			public void exec(CommandInvocation inv) {
				Inventory invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
				invent.add(new LootCrateItem(CrateType.DAILY));
				inv.event.getChannel().sendMessage("Test").queue();
			}
		});

		register(new MatchBasedCommand("inventory", "inv") {

			@Override
			public void exec(CommandInvocation inv) {

//				SHOW_ENTRIES: {
//					int page;
//					try {
//						page = Integer.parseInt(inv.args[0]);
//					} catch (NumberFormatException e) {
//						break SHOW_ENTRIES;
//					}
//
//					return;
//				}

				Inventory invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
				EmbedBuilder eb = new EmbedBuilder();

				eb.setAuthor(inv.event.getAuthor().getAsTag() + "'s Inventory", null,
						inv.event.getAuthor().getEffectiveAvatarUrl());
				eb.setDescription('*' + inv.event.getAuthor().getAsMention() + " has `" + invent.getEntryCount() + "` "
						+ (invent.getEntryCount() == 1 ? "type of item" : "different types of items") + " and `"
						+ invent.getTotalItemCount() + "` total items.*");
				print(invent.getPage(1, 9), eb);
				inv.event.getChannel().sendMessage(eb.build()).queue();
			}
		});

	}

	private final static EmbedBuilder print(List<Entry<?>> entries, EmbedBuilder builder) {
		for (Entry<?> e : entries) {
			BigInteger totalCount = e.getTotalCount();
			builder.addField(e.getIcon() + ' ' + e.getName(), " *You have [`" + totalCount + "`](https://clover.gartham.com 'Item ID: "
					+ e.getType() + ". Use the ID to get or interact with the item.') of this.*", true);
		}
		return builder;
	}
}
