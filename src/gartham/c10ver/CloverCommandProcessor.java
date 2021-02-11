package gartham.c10ver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.data.PropertyObject.Property;
import gartham.c10ver.economy.Account;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.economy.items.LootCrateItem;
import gartham.c10ver.economy.items.LootCrateItem.CrateType;
import gartham.c10ver.users.User;
import gartham.c10ver.utils.FormattingUtils;
import gartham.c10ver.utils.Paginator;
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
				if (u.timeSinceLastMonthly().toDays() < 30) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention()
									+ ", you must wait `" + FormattingUtils
											.formatLargest(Duration.ofDays(30).minus(u.timeSinceLastMonthly()), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.monthlyInvoked();
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
							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " `" + inv.args[0]
									+ "` is not a valid page.").queue();
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
							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " `" + inv.args[1]
									+ "` is not a valid page.").queue();
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
						inv.event
								.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " you only have `"
										+ maxPage + (maxPage == 1 ? "` page" : "` pages") + " in your inventory!")
								.queue();
					} else {
						EmbedBuilder eb = new EmbedBuilder();

						eb.setAuthor(inv.event.getAuthor().getAsTag() + "'s Inventory", null,
								inv.event.getAuthor().getEffectiveAvatarUrl());
						eb.setDescription('*' + inv.event.getAuthor().getAsMention() + " has `" + invent.getEntryCount()
								+ "` " + (invent.getEntryCount() == 1 ? "type of item" : "different types of items")
								+ " and `" + invent.getTotalItemCount() + "` total items.*\n\u200B");
						printEntries(pageItems, eb);
						eb.addField("",
								"You have **" + maxPage + "** page" + (maxPage == 1 ? "" : "s") + " in your inventory.",
								false);
//						eb.setFooter(
//								"You have " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " in your inventory.");
						inv.event.getChannel().sendMessage(eb.build()).queue();
					}
					return;
				}

				Entry<?> entry = invent.get(type);
				if (entry == null)
					inv.event.getChannel()
							.sendMessage(
									inv.event.getAuthor().getAsMention() + " you don't have any items of that type!")
							.queue();
				else {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor(inv.event.getAuthor().getAsTag() + "'s Inventory: " + entry.getName(), null,
							inv.event.getAuthor().getEffectiveAvatarUrl());
					eb.setDescription('*' + inv.event.getAuthor().getAsMention() + " has `" + entry.getTotalCount()
							+ "` of this item.*\n\u200B");
					int maxPage = Paginator.maxPage(9, entry.getStacks());
					if (page > maxPage) {
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + " you only have `" + maxPage
										+ (maxPage == 1 ? "` page" : "` pages") + " of that item in your inventory!")
								.queue();
						return;
					}
					List<? extends Entry<?>.ItemStack> list = Paginator.paginate(page, 9, entry.getStacks());
					printStacks(list, eb);
					eb.addField("",
							"You have **" + maxPage + "** page" + (maxPage == 1 ? "" : "s") + " in your inventory.",
							false);
//					eb.setFooter("You have " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " in your inventory.");
					inv.event.getChannel().sendMessage(eb.build()).queue();
				}
			}
		});

	}

	private final static EmbedBuilder printEntries(List<Entry<?>> entries, EmbedBuilder builder) {
		for (Entry<?> e : entries)
			builder.addField(e.getIcon() + ' ' + e.getName(),
					" *You have [`" + e.getTotalCount() + "`](https://clover.gartham.com 'Item ID: " + e.getType()
							+ ". Use the ID to get or interact with the item.') of this.*",
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
			builder.addField(i.getIcon() + ' ' + i.getName(), sb.toString(), true);
		}
		return builder;
	}

}
