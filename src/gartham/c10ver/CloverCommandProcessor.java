package gartham.c10ver;

import static gartham.c10ver.utils.Utilities.format;
import static org.alixia.javalibrary.JavaTools.maxPage;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.commands.SimpleCommandProcessor;
import gartham.c10ver.economy.users.UserAccount;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class CloverCommandProcessor extends SimpleCommandProcessor {

	private final Clover clover;

	private static final String BARS[][] = { { "<:HealthFront:856774887379959818>" },
			{ "<:HealthSectionEmpty:856778113206452254>", "<:HealthSection12_5p:856774887296991253>",
					"<:HealthSection25p:856774887423344660>", "<:HealthSection37_5p:856774887388610561>",
					"<:HealthSection50p:856774886998409268>", "<:HealthSection62_5p:856774887103266847>",
					"<:HealthSection75p:856774887179943937>", "<:HealthSection87_5p:856774887565033482>",
					"<:HealthSectionFull:856774887439990834>" },
			{ "<:HealthBackEmpty:856774887377076274>", "<:HealthBackFull:856774887137345547>" } };

	public CloverCommandProcessor(Clover clover) {
		this.clover = clover;
	}

	{

		help.addCommand("mults", "Lists the multipliers that affect your rewards!", "mults", "multipliers");

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
					if (bi.compareTo(BigInteger.ZERO) <= 0) {
						inv.event.getChannel().sendMessage(
								"You can't pay any less than " + Utilities.format(BigInteger.ONE) + " to another user.")
								.queue();
						return;
					}
					var mentionedUsers = inv.event.getMessage().getMentions().getUsers();
					if (mentionedUsers.size() != 1) {
						inv.event.getChannel().sendMessage("You need to specify one user to pay money to.").queue();
						return;
					}

					if (inv.event.getAuthor().getId().equals(mentionedUsers.get(0).getId())) {
						inv.event.getChannel().sendMessage("You can't pay yourself money... :thinking:").queue();
						return;
					}

					UserAccount payer = clover.getEconomy().getAccount(inv.event.getAuthor().getId()),
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
		register(new MatchBasedCommand("baltop", "leaderboard", "lb", "top") {

			private final Set<String> servers = new HashSet<>();

			private BigInteger getBal(Member member) {
				return clover.getEconomy().hasAccount(member.getId())
						? clover.getEconomy().getAccount(member.getId()).getBalance()
						: BigInteger.ZERO;
			}

			@Override
			public void exec(CommandInvocation inv) {
				if (inv.event.isFromGuild()) {
					if (inv.args.length > 1) {
						inv.event.getChannel().sendMessage("Too many arguments.").queue();
						return;
					}

					inv.event.getChannel().sendMessage("Sorting all members! (This may take a moment.)");
					if (!servers.contains(inv.event.getGuild().getId())) {
						servers.add(inv.event.getGuild().getId());
						inv.event.getGuild().findMembers(t -> !t.getUser().isBot()).onSuccess(t -> {
							try {

								List<Member> users = new ArrayList<>();
								for (Member m : t) {
									int search = Collections.binarySearch(users, m,
											((Comparator<Member>) (o1, o2) -> getBal(o1).compareTo(getBal(o2)))
													.reversed());
									users.add(search < 0 ? -search - 1 : search, m);
								}

								int page;
								PAGE_PARSER: if (inv.args.length == 1) {
									try {
										if ((page = Integer.parseInt(inv.args[0])) > 0)
											break PAGE_PARSER;
									} catch (NumberFormatException e) {
									}
									inv.event.getChannel()
											.sendMessage(
													inv.event.getAuthor().getAsMention() + ", that's not a valid page!")
											.queue();
									return;
								} else
									page = 1;

								int maxpage = maxPage(10, users);
								if (page > maxpage) {
									inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " there "
											+ (maxpage == 1 ? "is only `1` page" : "are only `" + maxpage + "` pages")
											+ " of people in the leaderboard!").queue();
									return;
								}

								EmbedBuilder eb = new EmbedBuilder();
								eb.setAuthor("Server Leaderboard", null, inv.event.getGuild().getIconUrl());
								StringBuilder sb = new StringBuilder();

								List<Member> paginate = JavaTools.paginate(page, 10, users);
								for (int i = 0; i < paginate.size(); i++) {
									var u = paginate.get(i);
									sb.append("`#" + (page * 10 - 9 + i) + "` " + u.getUser().getName() + "#"
											+ u.getUser().getDiscriminator() + ": "
											+ format(clover.getEconomy().getAccount(u.getId()).getBalance()) + "\n");
								}
								eb.addField("Page " + page + " Ranking", sb.toString(), false);
								eb.setFooter("Showing page " + page + " in the server leaderboard.");

								inv.event.getChannel().sendMessageEmbeds(eb.build()).queue();
							} finally {
								servers.remove(inv.event.getGuild().getId());
							}
						}).onError(t -> {
							servers.remove(inv.event.getGuild().getId());
							inv.event.getChannel()
									.sendMessage("An error occurred while querying discord for server members.")
									.queue();
						});
					}
				} else
					inv.event.getChannel().sendMessage("Please run this command in a server.").queue();
			}
		});

		help.addCommand("stats", "Shows a user's stats!", "stats [user]", "info");

		help.addCommand("pay", "Use this command to pay other people.", "pay (user) (amount)");
		help.addCommand("balance", "Tells you how rich you are.", "balance", "bal");
		help.addCommand("baltop", "Check out who the richest people in this server are!", "baltop [page]",
				"leaderboard");
	}

}
