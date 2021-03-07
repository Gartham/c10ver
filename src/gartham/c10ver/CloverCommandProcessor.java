package gartham.c10ver;

import static gartham.c10ver.economy.items.ItemBunch.of;
import static gartham.c10ver.utils.Utilities.format;
import static gartham.c10ver.utils.Utilities.listRewards;
import static gartham.c10ver.utils.Utilities.maxPage;
import static gartham.c10ver.utils.Utilities.paginate;

import java.awt.Color;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.util.Box;
import org.alixia.javalibrary.util.MultidimensionalMap;

import gartham.c10ver.commands.CommandHelpBook.ParentCommandHelp;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.commands.SimpleCommandProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageInputConsumer;
import gartham.c10ver.commands.subcommands.ParentCommand;
import gartham.c10ver.commands.subcommands.SubcommandInvocation;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.economy.Account;
import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.economy.Server;
import gartham.c10ver.economy.User;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.Inventory.Entry;
import gartham.c10ver.economy.items.utility.crates.DailyCrate;
import gartham.c10ver.economy.items.utility.crates.LootCrateItem;
import gartham.c10ver.economy.items.utility.crates.MonthlyCrate;
import gartham.c10ver.economy.items.utility.crates.WeeklyCrate;
import gartham.c10ver.economy.items.utility.foodstuffs.Foodstuff;
import gartham.c10ver.economy.questions.Question;
import gartham.c10ver.economy.questions.Question.Difficulty;
import gartham.c10ver.economy.server.ColorRole;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

public class CloverCommandProcessor extends SimpleCommandProcessor {

	private final Clover clover;

	public CloverCommandProcessor(Clover clover) {
		this.clover = clover;
	}

	{

//		register(new MatchBasedCommand("stats", "info") {
//
//			@Override
//			public void exec(CommandInvocation inv) {
//				net.dv8tion.jda.api.entities.User u;
//				if (inv.args.length > 0) {
//					String id = Utilities.parseMention(inv.args[0]);
//					if (id == null) {
//						inv.event.getChannel().sendMessage(
//								inv.event.getAuthor().getAsMention() + " ping who you want to see the stats of.")
//								.queue();
//						return;
//					} else {
//						try {
//							u = clover.getBot().retrieveUserById(id).complete();
//						} catch (NumberFormatException e) {
//							inv.event.getChannel()
//									.sendMessage(inv.event.getAuthor().getAsMention() + " that's not a valid mention.")
//									.queue();
//							return;
//						}
//						if (u == null) {
//							inv.event.getChannel()
//									.sendMessage(inv.event.getAuthor().getAsMention() + " that user couldn't be found.")
//									.queue();
//							return;
//						} else if (!clover.getEconomy().hasUser(u.getId())) {
//							inv.event.getChannel().sendMessage(u.getAsMention() + " doesn't have an account.").queue();
//							return;
//						}
//					}
//				} else if (!clover.getEconomy().hasUser(inv.event.getAuthor().getId())) {
//					inv.event.getChannel().sendMessage("You don't have an account.").queue();
//					return;
//				} else
//					u = inv.event.getAuthor();
//
//				EmbedBuilder eb = new EmbedBuilder();
//				eb.setAuthor(u.getAsTag() + "'s Stats!", null, u.getEffectiveAvatarUrl()).setColor(Color.blue);
//				// TODO Print stats.
//			}
//		});
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
					var reward = u.rewardAndSave((long) (Math.random() * 25 + 10), mult);

					Inventory invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
					var rewards = of(new DailyCrate());
					invent.add(rewards).save();
					u.save();

					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " is getting their daily rewards!\n\n**Rewards:**\n" + listRewards(reward, mult, rewards)
							+ "\nTotal Cloves: " + format(u.getAccount().getBalance())).queue();
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
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ Utilities.formatLargest(Duration.ofDays(7).minus(u.timeSinceLastWeekly()), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.weeklyInvoked();
					var mult = u.calcMultiplier(inv.event.getGuild());
					var amt = u.rewardAndSave((long) (Math.random() * 250 + 100), mult);

					Inventory invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
					var rewards = of(new WeeklyCrate());
					invent.add(rewards).save();
					u.save();

					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " is getting their weekly rewards!\n\n**Rewards:**\n" + listRewards(amt, mult, rewards)
							+ "\nTotal Cloves: " + format(u.getAccount().getBalance())).queue();
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
							.sendMessage(inv.event.getAuthor().getAsMention() + ", you must wait `"
									+ Utilities.formatLargest(Duration.ofDays(30).minus(u.timeSinceLastMonthly()), 3)
									+ "` before running that command.")
							.queue();
				} else {
					u.monthlyInvoked();

					var mult = u.calcMultiplier(inv.event.getGuild());
					var amt = u.rewardAndSave((long) (Math.random() * 10000 + 4000), mult);

					Inventory invent = clover.getEconomy().getInventory(inv.event.getAuthor().getId());
					var rewards = of(new MonthlyCrate());

					invent.add(rewards).save();
					u.save();

					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " is getting their monthly rewards!!!\n\n**Rewards:**\n" + listRewards(amt, mult, rewards)
							+ "\nTotal Cloves: " + format(u.getAccount().getBalance())).queue();
				}
			}
		});

		register(new ParentCommand("open", "use") {

			{
				new Subcommand("crate", "loot-crate") {
					@SuppressWarnings("unchecked")
					@Override
					protected void tailed(SubcommandInvocation inv) {
						if (inv.args.length == 0)
							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
									+ " please tell me what type of crate you want to open.").queue();
						else if (inv.args.length == 1) {
							if (clover.getEconomy().hasUser(inv.event.getAuthor().getId())) {
								var u = clover.getEconomy().getUser(inv.event.getAuthor().getId());
								var crateEntry = (Entry<LootCrateItem>) u.getInventory().get("loot-crate");
								if (crateEntry != null)
									for (var is : crateEntry.getStacks())
										if (is.getItem().getCrateType().equalsIgnoreCase(inv.args[0])) {
											LootCrateItem lci = is.getItem();
											var rew = lci.open();
											for (var m : rew.getMultipliers())
												u.addMultiplier(m);
											var totalMult = u.calcMultiplier(inv.event.getGuild());
											var totalCloves = u.rewardAndSave(rew.getCloves(), totalMult);
											for (var i : rew.getItemList())
												u.getInventory().add(i).save();
											is.removeAndSave(BigInteger.ONE);
											u.save();

											inv.event.getChannel()
													.sendMessage(
															inv.event.getAuthor().getAsMention() + " is opening a **"
																	+ lci.getCustomName() + "**!\n\n"
																	+ listRewards(rew, totalCloves,
																			u.getAccount().getBalance(), totalMult))
													.queue();
											return;
										}
							}
							inv.event.getChannel().sendMessage(
									inv.event.getAuthor().getAsMention() + " you don't have any crates of that type.")
									.queue();

						} else
							inv.event.getChannel().sendMessage(
									inv.event.getAuthor().getAsMention() + " too many args! Just provide a crate type!")
									.queue();
					}
				};
			}

			@Override
			protected void tailed(CommandInvocation inv) {
				if (inv.args.length == 0)
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " provide what item you want to open or use and try again.").queue();
				else if (inv.args.length == 1)
					if (clover.getEconomy().hasUser(inv.event.getAuthor().getId())) {
						var u = clover.getEconomy().getUser(inv.event.getAuthor().getId());
						var crateEntry = u.getInventory().get(inv.args[0]);
						if (crateEntry != null) {
							Entry<?>.ItemStack is = crateEntry.get(0);
							if (is.getItem() instanceof Foodstuff) {
								is.removeAndSave(BigInteger.ONE);
								var lci = (Foodstuff) is.getItem();
								var mult = lci.getMultiplier();
								lci.consume(u);
								u.save();

								inv.event.getChannel()
										.sendMessage(inv.event.getAuthor().getAsMention() + " you consumed some "
												+ lci.getEffectiveName() + " and received a multiplier: [**x"
												+ Utilities.multiplier(mult) + "**]!")
										.queue();
								return;
							}
						}
					}
				inv.event.getChannel()
						.sendMessage(inv.event.getAuthor().getAsMention()
								+ " you can't use that item! Either you don't have any of it, or it doesn't exist.")
						.queue();
			}
		});

		help.addCommand("mults", "Lists all of your active multipliers.", "mults", "multipliers");
		register(new MatchBasedCommand("mults", "multipliers") {

			@Override
			public void exec(CommandInvocation inv) {
				if (!clover.getEconomy().hasUser(inv.event.getAuthor().getId()))
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + " you don't have any multipliers yet.")
							.queue();
				else {
					var u = clover.getEconomy().getUser(inv.event.getAuthor().getId());
					var multipliers = u.getMultipliers();
					if (multipliers.isEmpty()) {
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + " you don't have any multipliers.")
								.queue();
					} else {
						StringBuilder sb = new StringBuilder();
						class MultConv {
							final Multiplier mult;

							public MultConv(Multiplier mult) {
								this.mult = mult;
							}

							@Override
							public boolean equals(Object obj) {
								return obj instanceof MultConv
										&& ((MultConv) obj).mult.getAmount().equals(mult.getAmount());
							}

							@Override
							public int hashCode() {
								return mult.getAmount().hashCode() * 31;
							}

						}
						var mm = JavaTools.frequencyMap(JavaTools.mask(multipliers, MultConv::new));
						if (!mm.isEmpty()) {
							for (var e : mm.entrySet()) {
								sb.append('(').append(e.getValue()).append("x) [**x")
										.append(e.getKey().mult.getAmount());
								if (e.getValue() == 1)
									sb.append("**] for ");
								else
									sb.append("**] for about ");
								sb.append(Utilities.formatLargest(e.getKey().mult.getTimeRemaining(), 2)).append('\n');
							}
							sb.append('\n');
						}
						sb.append("Total Personal Multiplier: [**x")
								.append(Utilities.multiplier(u.getPersonalTotalMultiplier())).append("**]");
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + "'s Multipliers: \n" + sb).queue();
					}
				}
			}
		});

//		register(new ParentCommand("shop", "market") {
//
//			@Override
//			protected void tailed(CommandInvocation inv) {
//				if (inv.event.isFromGuild())
//					// if (!clover.getEconomy().hasServer(inv.event.getGuild().getId())) {
//						inv.event.getChannel().sendMessage("There is nothing in the shop yet...").queue();
//					} else {
//						EmbedBuilder eb = new EmbedBuilder();
//					inv.event.getChannel().sendMessage("There is nothing in the shop yet...").queue();
//					}
//				else
//					inv.event.getChannel().sendMessage("You must be in a guild to use that command.").queue();
//			}
//		});
		register(new ParentCommand("color", "color-role") {

			@Override
			protected void tailed(CommandInvocation inv) {
				if (inv.args.length == 0) {
					var m = inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + " what color do you want?");
					if (clover.getEconomy().hasServer(inv.event.getGuild().getId())) {
						Server s = clover.getEconomy().getServer(inv.event.getGuild().getId());
						if (!s.getColorRoles().isEmpty()) {
							EmbedBuilder eb = new EmbedBuilder();
							StringBuilder sb = new StringBuilder();
							sb.append("Available Color Roles:");
							for (var e : s.getColorRoles().entrySet())
								sb.append("\n<@&").append(e.getKey()).append("> ").append(e.getValue().getName())
										.append(" **").append(format(e.getValue().getCost())).append("**");
							sb.append("\n\n**NOTE:** You currently must pay **each** time you change your role.");
							eb.setDescription(sb);
							m.embed(eb.build()).queue();
							return;
						}
					}
					inv.event.getChannel().sendMessage("There are no color roles set up for this server yet.").queue();
				} else if (inv.args.length == 1) {
					if (clover.getEconomy().hasServer(inv.event.getGuild().getId())) {
						Server s = clover.getEconomy().getServer(inv.event.getGuild().getId());
						if (!s.getColorRoles().isEmpty()) {
							var u = clover.getEconomy().getAccount(inv.event.getAuthor().getId());
							for (ColorRole cr : s.getColorRoles().values())
								if (cr.getName().equalsIgnoreCase(inv.args[0])) {
									Role role;
									try {
										role = inv.event.getGuild().getRoleById(cr.getID());
									} catch (NumberFormatException e) {
										e.printStackTrace();
										inv.event.getChannel().sendMessage(
												"There is something wrong with that role. Please contact staff.")
												.queue();
										return;
									}
									if (role == null) {
										inv.event.getChannel().sendMessage(
												"There is something wrong with that role. Please contact staff.")
												.queue();
										return;
									}
									if (inv.event.getMember().getRoles().contains(role)) {
										inv.event.getChannel().sendMessage("You already have that role.").queue();
										return;
									}
									if (inv.event.getMember().getTimeBoosted() != null || u.withdraw(cr.getCost())) {
										u.save();
										List<Role> roles = new ArrayList<>(inv.event.getMember().getRoles());
										for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();)
											if (s.getColorRoles().containsKey(iterator.next().getId()))
												iterator.remove();
										roles.add(role);
										try {
											inv.event.getGuild().modifyMemberRoles(inv.event.getMember(), roles)
													.queue();
										} catch (PermissionException e) {
											inv.event.getChannel().sendMessage(
													"I'm missing permissions to do that. Please tell staff! (**Don't run the command again,** you might lose cloves.)")
													.queue();
											return;
										}
										inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
												+ " your color is now " + cr.getName() + "!").queue();
									} else
										inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
												+ " you don't have enough money to apply that role!").queue();
									return;
								}
							inv.event.getChannel().sendMessage(
									inv.event.getAuthor().getAsMention() + " couldn't find a role for that color.")
									.queue();
							return;
						}
					}
					inv.event.getChannel().sendMessage("There are no color roles set up for this server yet.").queue();
				} else {
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " there are no color roles set up for this server yet.").queue();
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

					if (payer.pay(bi, recip))
						inv.event.getChannel()
								.sendMessage(inv.event.getAuthor().getAsMention() + ", you paid " + format(bi) + " to "
										+ mentionedUsers.get(0).getAsMention() + ". You now have "
										+ format(payer.getBalance()) + ".")
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
		register(new MatchBasedCommand("bal", "balance") {

			@Override
			public void exec(CommandInvocation inv) {
				if (inv.args.length > 0) {
					String id = Utilities.parseMention(inv.args[0]);
					if (id == null)
						inv.event.getChannel().sendMessage(
								inv.event.getAuthor().getAsMention() + " ping who you want to check the balance of.")
								.queue();
					else {
						net.dv8tion.jda.api.entities.User u;
						try {
							u = clover.getBot().retrieveUserById(id).complete();
						} catch (NumberFormatException e) {
							inv.event.getChannel()
									.sendMessage(inv.event.getAuthor().getAsMention() + " that's not a valid mention.")
									.queue();
							return;
						}
						if (u == null) {
							inv.event.getChannel()
									.sendMessage(inv.event.getAuthor().getAsMention() + " that user couldn't be found.")
									.queue();
						} else {
							if (clover.getEconomy().hasAccount(u.getId())) {
								var bal = clover.getEconomy().getAccount(u.getId()).getBalance();
								inv.event.getChannel().sendMessage(u.getAsMention() + " has **" + format(bal) + "** (`"
										+ NumberFormat.getNumberInstance().format(bal) + "`)").queue();
							} else {
								inv.event.getChannel().sendMessage(u.getAsMention() + " doesn't have an account.")
										.queue();
							}
						}
					}
				} else {
					BigInteger bal = clover.getEconomy().getAccount(inv.event.getAuthor().getId()).getBalance();
					inv.event
							.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + ", you have **"
									+ format(bal) + "** (`" + NumberFormat.getNumberInstance().format(bal) + "`)")
							.queue();
				}
			}
		});
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
								.sendMessage(inv.event.getAuthor().getAsMention() + " there "
										+ (maxpage == 1 ? "is only `1` page" : "are only `" + maxpage + "` pages")
										+ " of people in the leaderboard!")
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

		register(new ParentCommand("quiz") {

			class AskedQuiz {
				Question question;
				InputConsumer<MessageReceivedEvent> msgcons;
				InputConsumer<MessageReactionAddEvent> reaccons;

				public AskedQuiz(Question question, InputConsumer<MessageReceivedEvent> msgcons,
						InputConsumer<MessageReactionAddEvent> reaccons) {
					this.question = question;
					this.msgcons = msgcons;
					this.reaccons = reaccons;
				}

			}

			MultidimensionalMap<AskedQuiz> questionMap = new MultidimensionalMap<>(2);
			{
				new Subcommand("list") {

					@Override
					protected void tailed(SubcommandInvocation inv) {
						if (!clover.isDev(inv.event.getAuthor())) {
							inv.event.getChannel()
									.sendMessage(inv.event.getAuthor().getAsMention() + " you can't use that command.")
									.queue();
							return;
						}
						var u = clover.getEconomy().getUser(inv.event.getAuthor().getId());
						if (u.getQuestions().isEmpty()) {
							inv.event.getChannel().sendMessage(
									inv.event.getAuthor().getAsMention() + " you don't have any questions stored.")
									.queue();
						} else {
							int page;
							if (inv.args.length == 0)
								page = 1;
							else {
								try {
									page = Integer.parseInt(inv.args[0]);
								} catch (NumberFormatException e) {
									inv.event.getChannel()
											.sendMessage(inv.event.getAuthor().getAsMention()
													+ " this is not a valid question number: `"
													+ Utilities.strip(inv.args[0]) + '`')
											.queue();
									return;
								}
								if (page < 0) {
									inv.event.getChannel()
											.sendMessage(inv.event.getAuthor().getAsMention()
													+ " this is not a valid question number: `"
													+ Utilities.strip(inv.args[0]) + '`')
											.queue();
									return;
								}
							}
							List<Question> questions = paginate(page, 5, u.getQuestions());
							int mp = maxPage(5, u.getQuestions());
							if (questions == null) {
								inv.event.getChannel()
										.sendMessage(inv.event.getAuthor().getAsMention() + " there "
												+ (mp == 1 ? "is only `1` page!" : "are only `" + mp + "` pages!"))
										.queue();
							} else {
								StringBuilder sb = new StringBuilder();
								sb.append("**Page ").append(page).append(" of questions**\n");
								int temp = page;
								temp--;
								temp *= 5;
								temp++;
								for (var q : questions)
									sb.append("\n`Q").append(temp++).append("` ").append(q.getDifficulty())
											.append(" - ").append(format(q.getValue()));
								if (page == mp)
									sb.append("\n\nEnd of question list.");
								else
									sb.append("\n\nUse `quiz list ").append(page + 1).append("` to see the next page.");
								inv.event.getChannel().sendMessage(sb).queue();
							}
						}
					}
				};
				new Subcommand("new", "make", "create") {
					@Override
					protected void tailed(SubcommandInvocation inv) {
						if (!clover.isDev(inv.event.getAuthor())) {
							inv.event.getChannel()
									.sendMessage(inv.event.getAuthor().getAsMention() + " you can't use that command.")
									.queue();
							return;
						}
						if (inv.args.length != 2)
							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
									+ ", you need to provide a *value* and a *difficulty* in that command. After you do that, you'll get prompted for the question.")
									.queue();
						else {

							BigInteger value;
							try {
								value = new BigInteger(inv.args[0]);
							} catch (NumberFormatException e) {
								inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
										+ " your first argument should be a number! (It's the value of the question.)")
										.queue();
								return;
							}

							Difficulty difficulty;
							try {
								difficulty = Difficulty.valueOf(inv.args[1].toUpperCase());
							} catch (IllegalArgumentException e) {
								inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
										+ " your second argument should be a difficulty.").queue();
								return;
							}

							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
									+ " looks good so far! Please send your question as a message: ").queue();
							MessageInputConsumer inp = (event, eventHandler, ic) -> {
								event.getChannel().sendMessage(
										event.getAuthor().getAsMention() + " your question has been registered!")
										.queue();
								Question q = new Question(event.getMessage().getContentRaw(), value, difficulty);
								User user = clover.getEconomy().getUser(event.getAuthor().getId());
								user.getQuestions().add(q);
								user.save();
								return true;
							};
							clover.getEventHandler().getMessageProcessor()
									.registerInputConsumer(inp.filter(inv.event.getAuthor(), inv.event.getChannel()));
						}
					}
				};
				new Subcommand("view") {

					@Override
					protected void tailed(SubcommandInvocation inv) {
						if (inv.args.length == 1) {
							int numb;
							try {
								numb = Integer.parseInt(inv.args[0]) - 1;
							} catch (NumberFormatException e) {
								inv.event.getChannel()
										.sendMessage(inv.event.getAuthor().getAsMention()
												+ " this is not a valid question number: `"
												+ Utilities.strip(inv.args[0]) + '`')
										.queue();
								return;
							}
							if (numb < 0)
								inv.event.getChannel()
										.sendMessage(inv.event.getAuthor().getAsMention()
												+ " this is not a valid question number: `"
												+ Utilities.strip(inv.args[0]) + '`')
										.queue();
							else {
								var u = clover.getEconomy().getUser(inv.event.getAuthor().getId());
								var questions = u.getQuestions();
								if (numb >= questions.size())
									inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
											+ " you only have `" + questions.size() + "` questions!").queue();
								else {
									var q = questions.get(numb);
									inv.event.getChannel()
											.sendMessage(new EmbedBuilder().setColor(switch (q.getDifficulty()) {
									case EASY:
										yield Color.green;
									case MEDIUM:
										yield Color.yellow;
									case HARD:
										yield Color.red;
									default:
										yield Color.black;
									}).setAuthor(
											"Question #" + (numb + 1) + " [" + Utilities.format(q.getValue()) + ']')
													.addField("\u200B", q.getQuestion(), false).build())
											.queue();
								}
							}
						} else if (inv.args.length == 0)
							inv.event.getChannel()
									.sendMessage(
											inv.event.getAuthor().getAsMention() + " tell me which question to show.")
									.queue();
						else
							inv.event.getChannel()
									.sendMessage(inv.event.getAuthor().getAsMention() + " too many arguments! >:(")
									.queue();
					}
				};
				new Subcommand("delete", "remove", "del", "rem") {

					@Override
					protected void tailed(SubcommandInvocation inv) {
						if (inv.args.length == 1) {
							int numb;
							try {
								numb = Integer.parseInt(inv.args[0]) - 1;
							} catch (NumberFormatException e) {
								inv.event.getChannel()
										.sendMessage(inv.event.getAuthor().getAsMention()
												+ " this is not a valid question number: `"
												+ Utilities.strip(inv.args[0]) + '`')
										.queue();
								return;
							}
							if (numb < 0)
								inv.event.getChannel()
										.sendMessage(inv.event.getAuthor().getAsMention()
												+ " this is not a valid question number: `"
												+ Utilities.strip(inv.args[0]) + '`')
										.queue();
							else {
								var u = clover.getEconomy().getUser(inv.event.getAuthor().getId());
								var questions = u.getQuestions();
								if (numb >= questions.size())
									inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
											+ " you only have `" + questions.size() + "` questions!").queue();
								else {
									inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
											+ " removed question " + (numb + 1) + '.').queue();
									var q = questions.remove(numb);
									@SuppressWarnings("unchecked")
									Map<String, AskedQuiz> dim = (Map<String, AskedQuiz>) questionMap
											.readDim(inv.event.getAuthor().getId());
									if (dim != null)
										for (var e : dim.entrySet())
											if (e.getValue().question == q) {
												questionMap.remove(inv.event.getAuthor().getId(), e.getKey());
												clover.getEventHandler().getMessageProcessor()
														.removeInputConsumer(e.getValue().msgcons);
												clover.getEventHandler().getReactionAdditionProcessor()
														.removeInputConsumer(e.getValue().reaccons);
											}
									u.save();
								}
							}
						} else if (inv.args.length == 0)
							inv.event.getChannel()
									.sendMessage(
											inv.event.getAuthor().getAsMention() + " tell me which question to delete.")
									.queue();
						else
							inv.event.getChannel()
									.sendMessage(inv.event.getAuthor().getAsMention() + " too many arguments! >:(")
									.queue();
					}
				};
			}

			@Override
			protected void tailed(CommandInvocation inv) {
				if (!clover.isDev(inv.event.getAuthor())) {
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + " you can't use that command.").queue();
					return;
				}
				if (inv.args.length != 1)
					inv.event.getChannel().sendMessage("You need to tell me which question you want to use.").queue();
				else {
					int numb;
					try {
						numb = Integer.parseInt(inv.args[0]) - 1;
					} catch (NumberFormatException e) {
						inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
								+ " this is not a valid question number: `" + Utilities.strip(inv.args[0]) + '`')
								.queue();
						return;
					}
					if (questionMap.contains(inv.event.getAuthor().getId(), inv.event.getChannel().getId())) {
						inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
								+ " this will close your previous question. Continue? (yes/N)").queue();
						clover.getEventHandler().getMessageProcessor()
								.registerInputConsumer(((MessageInputConsumer) (event, eventHandler, consumer) -> {
									switch (event.getMessage().getContentRaw().toLowerCase()) {
									case "y":
									case "yes":
										questionMap.remove(inv.event.getAuthor().getId(),
												inv.event.getChannel().getId());
										inv.event.getChannel()
												.sendMessage(
														inv.event.getAuthor().getAsMention() + " question rescinded.")
												.queue();
										clover.getEventHandler().getMessageProcessor().removeInputConsumer(consumer);
										return true;
									case "n":
									case "no":
										inv.event.getChannel().sendMessage("Alright.").queue();
										clover.getEventHandler().getMessageProcessor().removeInputConsumer(consumer);
										return true;
									default:
										return false;
									}
								}).filter(inv.event.getAuthor().getId(), inv.event.getChannel().getId()));
					} else if (numb < 0)
						inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
								+ " this is not a valid question number: `" + Utilities.strip(inv.args[0]) + '`')
								.queue();
					else {
						var u = clover.getEconomy().getUser(inv.event.getAuthor().getId());
						var questions = u.getQuestions();
						if (numb >= questions.size())
							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " you only have `"
									+ questions.size() + "` questions!").queue();
						else {
							var q = questions.get(numb);
							Box<InputConsumer<MessageReactionAddEvent>> reactionHandler = new Box<>();
							Box<InputConsumer<MessageReceivedEvent>> messageHandler = new Box<>();
							reactionHandler.value = (event, eventHandler, consumer) -> {
								if (event.getChannel().getId().equals(inv.event.getChannel().getId())
										&& event.getUserId().equals(inv.event.getAuthor().getId())
										&& event.getReactionEmote().isEmoji()
										&& event.getReactionEmote().getEmoji().equals("\u2705")) {
									var user = event.getChannel().retrieveMessageById(event.getMessageId()).complete()
											.getAuthor();
									var u1 = clover.getEconomy().getUser(user.getId());
									var mult = u1.calcMultiplier(event.getGuild());
									var rewards = u1.rewardAndSave(q.getValue(), mult);

									String m = Utilities.multiplier(mult);

									String msg = user.getAsMention() + ", you got the question right and earned "
											+ rewards + " for answering it!";
									if (m != null)
										msg += "\n\nMultiplier: **" + m + "**.";

									questionMap.remove(inv.event.getAuthor().getId(), inv.event.getChannel().getId());

									clover.getEventHandler().getReactionAdditionProcessor()
											.removeInputConsumer(consumer);
									clover.getEventHandler().getMessageProcessor()
											.removeInputConsumer(messageHandler.value);

									event.getChannel().sendMessage(msg).queue();
									return true;
								}
								return false;
							};
							messageHandler.value = (event, eventHandler, consumer) -> {
								if (event.getChannel().getId().equals(inv.event.getChannel().getId())
										&& event.getAuthor().getId().equals(inv.event.getAuthor().getId())) {
									CommandInvocation ci = clover.getCommandParser()
											.parse(event.getMessage().getContentRaw(), event);
									if (ci == null)
										return false;
									if (ci.cmdName.equalsIgnoreCase("accept")) {
										if (ci.args.length == 0) {
											event.getChannel().sendMessage(event.getAuthor().getAsMention()
													+ " whose answer do you want to accept?").queue();
											return true;
										}
										var fid = Utilities.parseMention(ci.args[0]);
										if (fid == null)
											event.getChannel().sendMessage(event.getAuthor().getAsMention()
													+ " ping whoever got the right answer in the `accept` command.")
													.queue();
										else {
											var user = event.getJDA().retrieveUserById(fid).complete();
											if (user == null) {
												event.getChannel().sendMessage(event.getAuthor().getAsMention()
														+ " that person couldn't be found.").queue();
												return true;
											}
											var u1 = clover.getEconomy().getUser(fid);

											var mult = u1.calcMultiplier(event.getGuild());
											var rewards = u1.reward(q.getValue(), mult);

											String m = Utilities.multiplier(mult);

											String msg = user.getAsMention()
													+ ", you got the question right and earned " + format(rewards)
													+ " for answering it!";
											if (m != null)
												msg += "\n\nMultiplier: **" + m + "**.";

											questionMap.remove(inv.event.getAuthor().getId(),
													inv.event.getChannel().getId());

											clover.getEventHandler().getReactionAdditionProcessor()
													.removeInputConsumer(reactionHandler.value);
											clover.getEventHandler().getMessageProcessor()
													.removeInputConsumer(consumer);

											event.getChannel().sendMessage(msg).queue();

										}
										return true;
									} else if (ci.cmdName.equalsIgnoreCase("cancel")) {
										event.getChannel()
												.sendMessage(event.getAuthor().getAsMention() + " question cancelled.")
												.queue();
										questionMap.remove(inv.event.getAuthor().getId(),
												inv.event.getChannel().getId());
										clover.getEventHandler().getReactionAdditionProcessor()
												.removeInputConsumer(reactionHandler.value);
										clover.getEventHandler().getMessageProcessor().removeInputConsumer(consumer);
										return true;
									}
								}
								return false;
							};
							clover.getEventHandler().getReactionAdditionProcessor()
									.registerInputConsumer(reactionHandler.value);
							clover.getEventHandler().getMessageProcessor().registerInputConsumer(messageHandler.value);
							questionMap.put(new AskedQuiz(q, messageHandler.value, reactionHandler.value),
									inv.event.getAuthor().getId(), inv.event.getChannel().getId());
							inv.event.getChannel().sendMessage(new EmbedBuilder().setColor(switch (q.getDifficulty()) {
							case EASY:
								yield Color.green;
							case MEDIUM:
								yield Color.yellow;
							case HARD:
								yield Color.red;
							default:
								yield Color.black;
							}).setAuthor("Question #" + (numb + 1) + " [" + Utilities.format(q.getValue()) + ']')
									.addField("\u200B", q.getQuestion(), false).build()).queue();
						}
					}
				}
			}
		});
		register(new ParentCommand("setup") {

			private final ParentCommandHelp setupHelp = help.addParentCommand("setup",
					"Allows you to set up and configure the bot to work with a server. (__You must be a Clover Officer to access this command!__)");
			private final ParentCommandHelp configHelp = setupHelp.addParentSubcommand("configure",
					"Configures specific settings for Clover. This will let you change settings, set new settings, and clear old settings.",
					"config");
			{
				setupHelp.addSubcommand("register",
						"Registers a server (and possibly its general channel) with Clover. This must be run in the server that should be linked to Clover.",
						"setup register [general-channel]", "create", "new");
				setupHelp.addSubcommand("view",
						"Allows you to view configuration settings that Clover has stored for this server.",
						"setup view");

				new Subcommand("register", "create", "new") {
					@Override
					protected void tailed(SubcommandInvocation inv) {
						if (!inv.event.isFromGuild()) {
							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
									+ " you can only use that command in a server.").queue();
						} else {
							if (clover.getEconomy().hasServer(inv.event.getGuild().getId())) {
								inv.event.getChannel().sendMessage(
										inv.event.getAuthor().getAsMention() + " this server is already registered.")
										.queue();
							} else {
								var serv = clover.getEconomy().getServer(inv.event.getGuild().getId());
								if (inv.args.length == 1) {
									Object o;
									String cm = Utilities.parseChannelMention(inv.args[0]);
									if (cm == null) {
										inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
												+ " that's not a valid channel ID.").queue();
										return;
									}
									try {
										o = inv.event.getGuild().getTextChannelById(cm);
									} catch (NumberFormatException e) {
										inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
												+ " that's not a valid channel ID.").queue();
										return;
									}
									if (o == null) {
										inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
												+ " that's not a valid channel ID.").queue();
										return;
									}
									serv.setGeneralChannel(cm);
								} else if (inv.args.length != 0) {
									inv.event.getChannel().sendMessage(
											inv.event.getAuthor().getAsMention() + " too many arguments provided.")
											.queue();
								}
								inv.event.getChannel().sendMessage("Registered this server.").queue();
								serv.save();
							}
						}
					}
				};

				new Subcommand("view") {

					@Override
					protected void tailed(SubcommandInvocation inv) {
						if (inv.event.isFromGuild())
							if (inv.args.length != 0)
								inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
										+ " that subcommand doesn't take arguments.").queue();
							else if (clover.getEconomy().hasServer(inv.event.getGuild().getId())) {
								StringBuilder sb = new StringBuilder();
								Server s = clover.getEconomy().getServer(inv.event.getGuild().getId());
								sb.append("**Server Info:**");
								if (s.getGeneralChannel() != null)
									sb.append("\nGeneral Channel: <#").append(s.getGeneralChannel()).append('>');
								if (s.getSpamChannel() != null)
									sb.append("\nSpam Channel: <#").append(s.getSpamChannel()).append('>');
								if (s.getGamblingChannel() != null)
									sb.append("\nGambling Channel: <#").append(s.getGamblingChannel()).append('>');
								if (!s.getColorRoles().isEmpty()) {
									sb.append("\nColor Roles:");
									for (var e : s.getColorRoles().entrySet())
										sb.append("\n<@&").append(e.getKey()).append("> ")
												.append(e.getValue().getName()).append(" **")
												.append(format(e.getValue().getCost())).append("**");
								} else if (s.getGeneralChannel() == null && s.getSpamChannel() == null
										&& s.getGamblingChannel() == null)
									sb.append("\nNothing has been configured for this server yet.");
								EmbedBuilder eb = new EmbedBuilder().setDescription(sb.toString());
								inv.event.getChannel().sendMessage(eb.build()).queue();
							} else
								inv.event.getChannel().sendMessage("This server is not yet registered with me.")
										.queue();
						else
							inv.event.getChannel().sendMessage(
									inv.event.getAuthor().getAsMention() + " you can only run that in a server.")
									.queue();
					}
				};

				new Subcommand("configure", "config") {

					{
						configHelp.addSubcommand("set",
								"Sets the value of a specific property. Currently you can set the general, gambling, or spam channel.\nFor example, `setup config set general #main` will configure clover to use `#main` as the general channel.",
								"setup ... set (property) (value)");
						configHelp.addSubcommand("clear",
								"Clears the value of a property. Currently, you can clear the general, spam, and gambling channel properties, or the color role list property.\nExample: `setup config clear color-roles`",
								"setup ... clear (property)");
						configHelp.addSubcommand("add",
								"Adds a value to a property which holds multiple elements, (like the color role list).",
								"setup ... add (property) (...values)");
						configHelp.addSubcommand("remove",
								"Removes a SINGLE ELEMENT from a property that contains multiple elements. Ex: `setup config remove color-roles @Red`.\nTo completely clear a property, use the `clear` subcommand instead of the `remove` subcommand.",
								"setup ... remove (property) (value)");

						new Subcommand("set") {

							@Override
							protected void tailed(SubcommandInvocation inv) {
								if (inv.args.length == 0) {
									inv.event.getChannel()
											.sendMessage(
													inv.event.getAuthor().getAsMention() + " what do you want to set?")
											.queue();
								} else if (inv.args.length == 1) {
									inv.event.getChannel()
											.sendMessage(inv.event.getAuthor().getAsMention() + " provide a value.")
											.queue();
								} else if (inv.args.length == 2) {
									Server s = clover.getEconomy().getServer(inv.event.getGuild().getId());
									switch (inv.args[0]) {
									case "general-channel":
									case "general":
										CHANP: {
											String cm = Utilities.parseChannelMention(inv.args[1]);
											if (cm == null)
												break CHANP;
											Object o;
											try {
												o = inv.event.getGuild().getTextChannelById(cm);
											} catch (NumberFormatException e) {
												break CHANP;
											}
											if (o == null)
												break CHANP;
											s.setGeneralChannel(cm);
											inv.event.getChannel().sendMessage("General channel set to <#" + cm + ">.")
													.queue();
											break;
										}
										inv.event.getChannel().sendMessage(
												inv.event.getAuthor().getAsMention() + " that's not a valid channel.")
												.queue();
										return;
									case "gambling-channel":
									case "gambling":
										CHANP: {
											String cm = Utilities.parseChannelMention(inv.args[1]);
											if (cm == null)
												break CHANP;
											Object o;
											try {
												o = inv.event.getGuild().getTextChannelById(cm);
											} catch (NumberFormatException e) {
												break CHANP;
											}
											if (o == null)
												break CHANP;
											s.setGamblingChannel(cm);
											inv.event.getChannel().sendMessage("Gambling channel set to <#" + cm + ">.")
													.queue();
										}
										inv.event.getChannel().sendMessage(
												inv.event.getAuthor().getAsMention() + " that's not a valid channel.")
												.queue();
										return;
									case "spam-channel":
									case "spam":
										CHANP: {
											String cm = Utilities.parseChannelMention(inv.args[1]);
											if (cm == null)
												break CHANP;
											Object o;
											try {
												o = inv.event.getGuild().getTextChannelById(cm);
											} catch (NumberFormatException e) {
												break CHANP;
											}
											if (o == null)
												break CHANP;
											s.setSpamChannel(cm);
											inv.event.getChannel().sendMessage("Spam channel set to <#" + cm + ">.")
													.queue();
											break;
										}
										inv.event.getChannel().sendMessage(
												inv.event.getAuthor().getAsMention() + " that's not a valid channel.")
												.queue();
										return;
									default:
										inv.event.getChannel().sendMessage(
												inv.event.getAuthor().getAsMention() + " that isn't a valid property.")
												.queue();
										return;
									}
									s.save();
								} else {
									inv.event.getChannel()
											.sendMessage(inv.event.getAuthor().getAsMention() + " too many arguments.")
											.queue();
								}
							}
						};

						new Subcommand("clear") {

							@Override
							protected void tailed(SubcommandInvocation inv) {
								if (inv.args.length == 0)
									inv.event.getChannel().sendMessage(
											inv.event.getAuthor().getAsMention() + " what do you want to clear?")
											.queue();
								else if (inv.args.length == 1) {
									Server s = clover.getEconomy().getServer(inv.event.getGuild().getId());
									switch (inv.args[0]) {
									case "general-channel":
									case "general":
										s.setGeneralChannel(null);
										inv.event.getChannel().sendMessage("Unregistered the general channel.").queue();
										break;
									case "gambling-channel":
									case "gambling":
										s.setGamblingChannel(null);
										inv.event.getChannel().sendMessage("Unregistered the gambling channel.")
												.queue();
										break;
									case "spam-channel":
									case "spam":
										s.setSpamChannel(null);
										inv.event.getChannel().sendMessage("Unregistered the spam channel.").queue();
										break;
									case "color-roles":
									case "color-role":
									case "colorrole":
										s.setColorRoles(new HashMap<>());
										inv.event.getChannel().sendMessage("Cleared the color role list.").queue();
										break;
									default:
										inv.event.getChannel().sendMessage(
												inv.event.getAuthor().getAsMention() + " that isn't a valid property.")
												.queue();
										return;
									}
									s.save();
								} else
									inv.event.getChannel().sendMessage(
											inv.event.getAuthor().getAsMention() + " what do you want to clear?")
											.queue();
							}
						};

						new Subcommand("add") {

							@Override
							protected void tailed(SubcommandInvocation inv) {
								if (inv.args.length == 0) {
									inv.event.getChannel().sendMessage(
											inv.event.getAuthor().getAsMention() + " what do you want to add to?")
											.queue();
								} else if (inv.args.length == 1) {
									inv.event.getChannel()
											.sendMessage(
													inv.event.getAuthor().getAsMention() + " provide a value to add.")
											.queue();
								} else {
									Server s = clover.getEconomy().getServer(inv.event.getGuild().getId());
									switch (inv.args[0]) {
									case "color-roles":
									case "color-role":
									case "colorrole":
										if (inv.args.length == 2) {
											inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
													+ " provide a name and cost for the role.").queue();
											return;
										} else if (inv.args.length == 3) {
											inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
													+ " provide a cost to for the role.").queue();
											return;
										} else if (inv.args.length == 4) {
											ROLEP: {
												String cm = Utilities.parseChannelMention(inv.args[1]);
												if (cm == null)
													cm = Utilities.strip(inv.args[1]);
												Object o;
												try {
													o = inv.event.getGuild().getRoleById(cm);
												} catch (NumberFormatException e) {
													break ROLEP;
												}
												if (o == null)
													break ROLEP;
												BigInteger cost;
												try {
													cost = new BigInteger(inv.args[3]);
												} catch (NumberFormatException e) {
													inv.event.getChannel().sendMessage(
															"Your last argument when adding a color role must be a cost.")
															.queue();
													return;
												}
												if (s.getColorRoles().isEmpty())
													s.setColorRoles(new HashMap<>());
												s.getColorRoles().put(cm,
														new ColorRole(Utilities.strip(inv.args[2]), cm, cost));
												inv.event.getChannel().sendMessage("Added the role successfully.")
														.queue();
												break;
											}
											inv.event.getChannel().sendMessage(
													inv.event.getAuthor().getAsMention() + " that's not a valid role.")
													.queue();
											return;
										} else {
											inv.event.getChannel().sendMessage(
													inv.event.getAuthor().getAsMention() + " too many arguments.")
													.queue();
											return;
										}
									default:
										inv.event.getChannel().sendMessage(
												inv.event.getAuthor().getAsMention() + " that isn't a valid property.")
												.queue();
										return;
									}
									s.save();
								}
							}
						};

						new Subcommand("remove") {

							@Override
							protected void tailed(SubcommandInvocation inv) {
								if (inv.args.length == 0)
									inv.event.getChannel().sendMessage(
											inv.event.getAuthor().getAsMention() + " what do you want to remove from?")
											.queue();
								else if (inv.args.length == 1)
									inv.event.getChannel().sendMessage(
											inv.event.getAuthor().getAsMention() + " provide a value to remove.")
											.queue();
								else if (inv.args.length == 2) {
									Server s = clover.getEconomy().getServer(inv.event.getGuild().getId());
									switch (inv.args[0]) {
									case "color-roles":
									case "color-role":
									case "colorrole":
										ROLEP: {
											String cm = Utilities.parseRoleMention(inv.args[1]);
											if (cm == null)
												cm = inv.args[1];
											Object o;
											try {
												o = inv.event.getGuild().getRoleById(cm);
											} catch (NumberFormatException e) {
												break ROLEP;
											}
											if (o == null)
												break ROLEP;
											if (s.getColorRoles().containsKey(cm)) {
												s.getColorRoles().remove(cm);
												inv.event.getChannel().sendMessage("Removed the role successfully.")
														.queue();
											} else {
												inv.event.getChannel()
														.sendMessage("That role is not in the color roles list.")
														.queue();
												return;
											}
											break;
										}
										inv.event.getChannel().sendMessage(
												inv.event.getAuthor().getAsMention() + " that's not a valid role.")
												.queue();
										return;
									default:
										inv.event.getChannel().sendMessage(
												inv.event.getAuthor().getAsMention() + " that isn't a valid property.")
												.queue();
										return;
									}
									s.save();
								} else
									inv.event.getChannel()
											.sendMessage(inv.event.getAuthor().getAsMention() + " too many arguments.")
											.queue();
							}
						};
					}

					@Override
					protected void tailed(SubcommandInvocation inv) {
						configHelp.print(inv.event.getChannel());
					}
				};
			}

			public boolean match(CommandInvocation inv) {
				return super.match(inv) && clover.isDev(inv.event.getAuthor());
			}

			@Override
			protected void tailed(CommandInvocation inv) {
				setupHelp.print(inv.event.getChannel());
			}
		});

		register(new MatchBasedCommand("trade") {

			@Override
			public void exec(CommandInvocation inv) {
				if (inv.args.length == 0)
					inv.event.getChannel().sendMessage(
							inv.event.getAuthor().getAsMention() + " you need to @mention whom you want to trade with.")
							.queue();
				else if (inv.args.length > 1)
					inv.event.getChannel()
							.sendMessage(
									inv.event.getAuthor().getAsMention() + " that command only takes one argument!")
							.queue();
				else {
					String id = Utilities.parseMention(inv.args[0]);
					if (id == null)
						inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
								+ " please @mention whomever you want to trade with.").queue();
					else {
						var u = inv.event.getGuild().getMemberById(id);
						if (u == null)
							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
									+ " that user is not a member of this server. :(").queue();
						else {
							inv.event.getChannel()
									.sendMessage(inv.event.getAuthor().getAsMention()
											+ " great! What do you want to give to this person?\n1. Cloves\n2. Item(s)")
									.queue();
							MessageInputConsumer mic = new MessageInputConsumer() {

								@Override
								public boolean consume(MessageReceivedEvent event,
										InputProcessor<? extends MessageReceivedEvent> eventHandler,
										InputConsumer<MessageReceivedEvent> consumer) {

									return false;
								}

							};
						}
					}
				}
			}
		});

//		help.addCommand("stats", "Shows a user's stats!", "stats [user]", "info");
		help.addCommand("daily", "Receive daily rewards! You can only run this once a day.", "daily");
		help.addCommand("weekly", "Receive weekly rewards! You can only run this once a day.", "weekly");
		help.addCommand("monthly", "Receive monthly rewards! You can only run this once a day.", "monthly");
		help.addCommand("open", "Open a crate or loot box, or use an item! Use this to open `crate`s or use `food`.",
				"open [item-type] (item)", "use");
		help.addCommand("mults", "Shows you what multipliers you have active.", "mults");
		help.addCommand("color", "Lets you purchase a color role.", "color ", "color-role");
		help.addCommand("pay", "Use this command to pay other people.", "pay (user) (amount)");
		help.addCommand("balance", "Tells you how rich you are.", "balance", "bal");
		help.addCommand("baltop", "Check out who the richest people in this server are!", "baltop [page]",
				"leaderboard");
		help.addCommand("inventory", "Shows your inventory.", "inventory [item-id] [page]", "inv");
		{
			var quizHelp = help.addParentCommand("quiz",
					"Lets you make, see, and give quizzes! (You must be a Clover Officer to access this command!)");
			quizHelp.addSubcommand("list", "Lists your questions if you have any registered.", "quiz list [page]",
					"view");
			quizHelp.addSubcommand("new", "Walks you through creating a new quiz question.",
					"quiz new (value) (difficulty)", "make", "create");
			quizHelp.addSubcommand("delete", "Use this to get rid of any of your questions.",
					"quiz delete (question-number)", "remove", "del", "rem");
		}
		// setup cmd help inside command object.
		help.addCommand("trade",
				"Starts a trade with another user. Trades let you securely exchange items, cloves, or other tradeable possessions.",
				"trade (@user)");
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
