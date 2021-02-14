package gartham.c10ver;

import static gartham.c10ver.economy.items.ItemBunch.of;
import static gartham.c10ver.utils.Utilities.format;
import static gartham.c10ver.utils.Utilities.listRewards;
import static gartham.c10ver.utils.Utilities.maxPage;
import static gartham.c10ver.utils.Utilities.paginate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import gartham.c10ver.commands.CommandHelpBook;
import gartham.c10ver.commands.CommandHelpBook.CommandHelp;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.economy.Account;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.economy.items.LootCrateItem;
import gartham.c10ver.economy.items.LootCrateItem.CrateType;
import gartham.c10ver.users.User;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CloverCommandProcessor extends CommandProcessor {

	private final Clover clover;
	private final CommandHelpBook help = new CommandHelpBook(3);

	public CloverCommandProcessor(Clover clover) {
		this.clover = clover;
	}

	public void printHelp(EmbedBuilder builder, CommandHelp help) {
		this.help.print(builder, help);
	}

	public void printHelp(MessageChannel channel, CommandHelp help) {
		this.help.print(channel, help);
	}

	public void printHelp(MessageChannel channel, int page) {
		help.print(channel, page);
	}

	public boolean printHelp(MessageChannel channel, String command, boolean allowAliases, boolean ignoreCase) {
		return help.print(channel, command, allowAliases, ignoreCase);
	}

	{
		final CommandHelp helpCommandHelp = help.addCommand("help", "Shows help for commands.",
				"help [page-number|command-name]", "?");
		register(new MatchBasedCommand("help", "?") {
			@Override
			public void exec(CommandInvocation inv) {
				int page = 1;
				FIRST_ARG: if (inv.args.length == 1) {
					String arg;
					if (!inv.args[0].startsWith("\\"))
						try {
							page = Integer.parseInt(inv.args[0]);
							break FIRST_ARG;
						} catch (final NumberFormatException e) {
							arg = inv.args[0];
						}
					else
						arg = inv.args[0].substring(1);
					if (!printHelp(inv.event.getChannel(), arg, true, true))
						inv.event.getChannel()
								.sendMessage("No command with the name or alias: \"" + arg + "\" was found.").queue();
					return;
				} else if (inv.args.length > 1) {
					inv.event.getChannel().sendMessage("Illegal number of arguments for command: " + inv.cmdName)
							.queue();
					printHelp(inv.event.getChannel(), helpCommandHelp);
					return;
				}
				printHelp(inv.event.getChannel(), page);

			}
		});

		help.addCommand("daily", "Receive daily rewards! You can only run this once a day.", "daily");
		register(new MatchBasedCommand("daily") {
			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();

				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastDaily().toDays() < 1)
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ Utilities.formatLargest(Duration.ofDays(1).minus(u.timeSinceLastDaily()), 3)
									+ "` before running that command.")
							.queue();
				else {
					u.dailyInvoked();
					var mult = u.calcMultiplier(inv.event.getGuild());
					var reward = u.reward((long) (Math.random() * 25 + 10), mult);
					u.getAccount().save();

					Inventory invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
					var rewards = of(new LootCrateItem(CrateType.DAILY));
					invent.add(rewards).save();
					u.save();

					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " is getting their daily rewards!\n\n**Rewards:**\n" + listRewards(reward, mult, rewards)
							+ "\nTotal Cloves: " + format(u.getAccount().getBalance())).queue();
				}

			}
		});

		help.addCommand("weekly", "Receive weekly rewards! You can only run this once a day.", "weekly");
		register(new MatchBasedCommand("weekly") {
			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastWeekly().toDays() < 7) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ Utilities.formatLargest(Duration.ofDays(7).minus(u.timeSinceLastWeekly()), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.weeklyInvoked();
					var mult = u.calcMultiplier(inv.event.getGuild());
					var amt = u.reward((long) (Math.random() * 250 + 100), mult);
					u.getAccount().save();

					Inventory invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
					var rewards = of(new LootCrateItem(CrateType.WEEKLY));
					invent.add(rewards).save();
					u.save();

					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " is getting their weekly rewards!\n\n**Rewards:**\n" + listRewards(amt, mult, rewards)
							+ "\nTotal Cloves: " + format(u.getAccount().getBalance())).queue();
				}
			}
		});

		help.addCommand("monthly", "Receive monthly rewards! You can only run this once a day.", "monthly");
		register(new MatchBasedCommand("monthly") {
			@Override
			public void exec(CommandInvocation inv) {
				String userid = inv.event.getAuthor().getId();
				User u = clover.getEconomy().getUser(userid);
				if (u.timeSinceLastMonthly().toDays() < 30) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ Utilities.formatLargest(Duration.ofDays(30).minus(u.timeSinceLastMonthly()), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.monthlyInvoked();

					var mult = u.calcMultiplier(inv.event.getGuild());
					var amt = u.reward((long) (Math.random() * 10000 + 4000), mult);
					u.getAccount().save();

					Inventory invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
					var rewards = of(new LootCrateItem(CrateType.MONTHLY));

					invent.add(rewards).save();
					u.save();

					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " is getting their monthly rewards!!!\n\n**Rewards:**\n" + listRewards(amt, mult, rewards)
							+ "\nTotal Cloves: " + format(u.getAccount().getBalance())).queue();
				}
			}
		});

		// TODO pay askdjflaskjhfd@Bob12987u1kmfdlskjflds 500
		help.addCommand("pay", "Use this command to pay other people.", "pay (user) (amount)");
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

					if (payer.pay(bi, recip))
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + ", you paid `" + format(bi)
										+ "` to " + mentionedUsers.get(0).getAsMention() + ". You now have `"
										+ format(payer.getBalance()) + "`.")
								.queue();
					else
						inv.event.getChannel()
								.sendMessage(
										inv.event.getAuthor().getAsMention() + ", you do not have enough money to pay `"
												+ bi + "` to " + mentionedUsers.get(0).getAsMention() + '.')
								.queue();
					payer.save();
					recip.save();
				}
			}
		});

		help.addCommand("balance", "Tells you how rich you are.", "balance", "bal");
		register(new MatchBasedCommand("bal", "balance") {

			@Override
			public void exec(CommandInvocation inv) {
				inv.event.getChannel()
						.sendMessage(inv.event.getAuthor().getAsMention() + ", you have "
								+ format(clover.getEconomy().getAccount(inv.event.getAuthor().getId()).getBalance()))
						.queue();
			}
		});

		help.addCommand("leaderboard", "Check out who the richest people in this server are!", "leaderboard [page]",
				"baltop");
		register(new MatchBasedCommand("baltop", "leaderboard") {

			@Override
			public void exec(CommandInvocation inv) {
				if (inv.event.isFromGuild()) {
					if (inv.args.length > 1) {
						inv.event.getChannel().sendMessage("Too many arguments.").queue();
						return;
					}
					List<Member> users = new ArrayList<>();
					inv.event.getGuild().loadMembers(new Consumer<Member>() {
						@Override
						public void accept(Member t) {
							int search = Collections
									.binarySearch(users, t,
											((Comparator<Member>) (o1, o2) -> clover.getEconomy().getUser(o1.getId())
													.getAccount().getBalance().compareTo(clover.getEconomy()
															.getUser(o2.getId()).getAccount().getBalance()))
																	.reversed());
							users.add(search < 0 ? -search - 1 : search, t);
						}
					});
					int page;
					PAGE_PARSER: if (inv.args.length == 1) {
						try {
							if ((page = Integer.parseInt(inv.args[0])) > 0)
								break PAGE_PARSER;
						} catch (NumberFormatException e) {
						}
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + ", that's not a valid page!")
								.queue();
						return;
					} else
						page = 1;

					int maxpage = maxPage(10, users);
					if (page > maxpage) {
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + " there are only `" + maxpage
										+ (maxpage == 1 ? "` page" : "` pages") + " of people in the leaderboard!")
								.queue();
						return;
					}

					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor("Server Leaderboard", null, inv.event.getGuild().getIconUrl());
					StringBuilder sb = new StringBuilder();

					List<Member> paginate = paginate(page, 10, users);
					for (int i = 0; i < paginate.size(); i++) {
						var u = paginate.get(i);
						sb.append("`#" + (i + 1) + "` " + u.getUser().getName() + "#" + u.getUser().getDiscriminator()
								+ ": " + format(clover.getEconomy().getAccount(u.getId()).getBalance()) + "\n");
					}
					eb.addField("Page " + page + " Ranking", sb.toString(), false);
					eb.setFooter("Showing page " + page + " in the server leaderboard.");

					inv.event.getChannel().sendMessage(eb.build()).queue();
				} else
					inv.event.getChannel().sendMessage("Please run this command in a server.").queue();
			}
		});

		help.addCommand("inventory", "Shows your inventory.", "inventory [item-id] [page]", "inv");
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
					int maxPage = Utilities.maxPage(9, entry.getStacks());
					if (page > maxPage) {
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + " you only have `" + maxPage
										+ (maxPage == 1 ? "` page" : "` pages") + " of that item in your inventory!")
								.queue();
						return;
					}
					List<? extends Entry<?>.ItemStack> list = Utilities.paginate(page, 9, entry.getStacks());
					printStacks(list, eb);
					eb.addField("", "You have **" + maxPage + "** page" + (maxPage == 1 ? "" : "s")
							+ " of this item in your inventory.", false);
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
			builder.addField(i.getIcon() + ' ' + i.getCustomName(), sb.toString(), true);
		}
		return builder;
	}

}
