package gartham.c10ver.clovercmds;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.CommandHelpBook;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.subcommands.ParentCommand;
import gartham.c10ver.commands.subcommands.SubcommandInvocation;
import gartham.c10ver.economy.privatechannels.PrivateChannel;
import gartham.c10ver.economy.users.EconomyUser;
import gartham.c10ver.economy.users.UserAccount;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class PrivateChannelCommand extends ParentCommand {

	private static final String PRIVATE_CHANNEL_FILE_NAMESPACE = "private-channels";
	private final Clover clover;

	public PrivateChannelCommand(Clover clover, CommandHelpBook help) {
		super("pc", "private-channel");
		this.clover = clover;

		File channels = clover.getRandStorage(PRIVATE_CHANNEL_FILE_NAMESPACE + "/channels");
		if (channels.isDirectory())
			for (File f : channels.listFiles()) {
				PrivateChannel pc;
				try {
					pc = PrivateChannel.load(f, clover);
				} catch (Exception e) {
					System.err.println("Failed to load a private channel: " + f);
					e.printStackTrace();
					continue;
				}
				putChannel(pc);
			}

		var h = help.addParentCommand("pc",
				"Allows you to purchase a private channel for you and your friends! Channels have a **tax** of "
						+ Utilities.CURRENCY_SYMBOL
						+ " 25K (+5K/invitee) that is charged at every hour. (This cost will be reduced in the future.)",
				"pc (name)", "private-channel");
		h.addSubcommand("list", "Shows you which private channels you have available for use.", "list", "show", "view");
		h.addSubcommand("buy", "Buy a new private channel. This costs " + Utilities.CURRENCY_SYMBOL
				+ " 12.5K and incurs a " + Utilities.CURRENCY_SYMBOL + " 25K tax.", "buy (name)", "new");
		h.addSubcommand("invite",
				"Adds people to your private channel. Doing this costs " + Utilities.CURRENCY_SYMBOL
						+ " 2.5K and will incur a " + Utilities.CURRENCY_SYMBOL + " 5K tax.",
				"add (@user) (#channel)", "register", "add");
		h.addSubcommand("delete",
				"Deletes a private channel. This stops it from charging you tax, but deletes the channel so that you'll no longer have access to it.",
				"delete (#channel)", "remove");
	}

//	private final Map<TextChannel, User> channels = new HashMap<>();

	private final Map<String, List<PrivateChannel>> channels = new HashMap<>();
	private final List<TextChannel> deletedChannels = new ArrayList<>();
	private final Timer t = new Timer();

	private void putChannel(PrivateChannel pc) {
		var cs = channels.get(pc.getOwnerID());
		if (cs == null)
			channels.put(pc.getOwnerID(), cs = new ArrayList<>());
		cs.add(pc);
	}

	{

		var c = Calendar.getInstance();
		c.setLenient(true);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 1);
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					synchronized (channels) {
						System.out.println("Taxing all private channel customers!");
						LIST: for (Iterator<List<PrivateChannel>> i = channels.values().iterator(); i.hasNext();)
							try {
								var l = i.next();
								for (Iterator<PrivateChannel> iterator = l.iterator(); iterator.hasNext();)
									try {
										var pc = iterator.next();
										TextChannel pcchan = pc.getDiscordChannel();
										if (pcchan == null) {
											iterator.remove();
											pc.getSaveLocation().delete();
											if (l.isEmpty()) {
												i.remove();
												continue LIST;
											}
											continue;
										}
										long cost = pc.cost();
										if (!pc.getOwner().getAccount().withdraw(cost)) {
											pcchan.sendMessage(
													"The owner of this channel did not have enough money to support it. It has been queued for deletion!!!")
													.queue();
											deletedChannels.add(pcchan);
											pc.getOwner().getUser().openPrivateChannel().complete()
													.sendMessage("Private channel taxes came around ("
															+ pcchan.getGuild().getName()
															+ "), and unfortunately you didn't have enough cloves in your account to support your private channel: <#"
															+ pcchan.getId() + ">. I am truly sorry. :pensive:")
													.queue();
											pcchan.getMemberPermissionOverrides().forEach(a -> a.delete().queue());
											iterator.remove();
											pc.getSaveLocation().delete();
											if (l.isEmpty())
												i.remove();
										} else {
											pcchan.sendMessage("Tax is being collected! **" + Utilities.CURRENCY_SYMBOL
													+ ' ' + cost + "** has been taken from "
													+ pc.getOwner().getUser().getAsTag()
													+ "'s account for upkeep and room-size (number of channel members).")
													.queue();
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
							} catch (Exception e) {
								e.printStackTrace();
							}
						System.out.println("Finished Taxing!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, c.getTime(), 3600000);

		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.SECOND, 5);
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				synchronized (channels) {
					for (var tc : deletedChannels)
						try {
							tc.delete().queue();
						} catch (ErrorResponseException e) {
							if (e.getErrorResponse() != ErrorResponse.UNKNOWN_CHANNEL) {
								System.out.println("Failed to delete a private channel, (Name=" + tc.getName() + ", ID="
										+ tc.getId() + "), for a reason other than it already being deleted: ");
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			}
		}, c.getTime(), 86400000);

		new Subcommand("delete", "remove") {

			@Override
			protected void tailed(SubcommandInvocation inv) {
				if (inv.args.length == 0)
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " you need to ping/mention a private channel you own.").queue();
				else if (inv.args.length != 1)
					inv.event.getChannel()
							.sendMessage(
									inv.event.getAuthor().getAsMention() + " this command only takes one argument.")
							.queue();
				else {

					String ment = Utilities.parseChannelMention(inv.args[0]);
					if (ment == null)
						inv.event.getChannel().sendMessage(
								inv.event.getAuthor().getAsMention() + " couldn't find any channel by that mention.")
								.queue();
					else {
						var l = channels.get(inv.event.getAuthor().getId());
						if (l == null || l.isEmpty())
							inv.event.getChannel().sendMessage("You don't have any private channels.").queue();
						else {
							for (Iterator<PrivateChannel> iterator = l.iterator(); iterator.hasNext();) {
								var pc = iterator.next();
								if (pc.getDiscordChannel().getId().equals(ment)) {
									deletedChannels.add(pc.getDiscordChannel());
									pc.getDiscordChannel().getMemberPermissionOverrides()
											.forEach(a -> a.delete().queue());
									iterator.remove();
									pc.getSaveLocation().delete();
									if (l.isEmpty())
										channels.remove(inv.event.getAuthor().getId());
									inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
											+ " you successfully deleted your channel.").queue();
									return;
								}
							}
							inv.event.getChannel().sendMessage("You don't own that channel.").queue();
						}
					}
				}
			}
		};

		new Subcommand("invite", "register", "add") {

			@Override
			protected void tailed(SubcommandInvocation inv) {
				var l = channels.get(inv.event.getAuthor().getId());

				if (inv.args.length < 2)
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " you need to provide a user to add and mention a private channel to add them to.")
							.queue();
				else if (inv.args.length != 2)
					inv.event.getChannel().sendMessage(
							inv.event.getAuthor().getAsMention() + " this command requires exactly 2 arguments.")
							.queue();
				else if (l == null || l.isEmpty())
					inv.event.getChannel().sendMessage(inv.event.getAuthor() + ", you don't have any private channels.")
							.queue();
				else {
					var us = Utilities.parseMention(inv.args[0]);
					if (us == null)
						inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
								+ ", couldn't find a user by that ping. (Make sure you're pinging the user in your first argument.)")
								.queue();
					else {
						net.dv8tion.jda.api.entities.User u;
						try {
							u = inv.event.getJDA().retrieveUserById(us).complete();
						} catch (Exception e) {
							inv.event.getChannel().sendMessage("Failed to find a user by that ping.").queue();
							return;
						}
						var pcid = Utilities.parseChannelMention(inv.args[1]);
						if (pcid == null)
							inv.event.getChannel().sendMessage("Couldn't find any channels by that mention.").queue();
						else {
							for (Iterator<PrivateChannel> iterator = l.iterator(); iterator.hasNext();) {
								var pc = iterator.next();
								if (pc.getDiscordChannel() == null) {
									iterator.remove();
									pc.getSaveLocation().delete();
								} else if (pc.getDiscordChannel().getId().equals(pcid)) {
									if (pc.getUsers().contains(u.getId()))
										inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
												+ " that user already has access to that channel. (If they can't access it, contact a staff member!")
												.queue();
									else {
										var acc = clover.getEconomy().getAccount(inv.event.getAuthor().getId());
										if (acc.withdraw(2500)) {
											pc.getUsers().add(u.getId());
											pc.getDiscordChannel()
													.createPermissionOverride(
															pc.getDiscordChannel().getGuild().getMember(u))
													.setAllow(Permission.CREATE_INSTANT_INVITE,
															Permission.MESSAGE_ADD_REACTION,
															Permission.MESSAGE_ATTACH_FILES,
															Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
															Permission.MESSAGE_WRITE, Permission.MESSAGE_READ)
													.queue();
											pc.save();
											inv.event.getChannel()
													.sendMessage("That user was added to your private channel!")
													.queue();
										} else
											inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
													+ ", you don't have enough cloves to invite a user to your private channel.")
													.queue();
									}

									return;
								}
								if (l.isEmpty()) {
									channels.remove(inv.event.getAuthor().getId());
									inv.event.getChannel()
											.sendMessage(
													inv.event.getAuthor() + ", you don't have any private channels.")
											.queue();
									return;
								}
							}
							inv.event.getChannel().sendMessage("Couldn't find any private channels by that ping.")
									.queue();
						}
					}
				}
			}
		};

		new Subcommand("list", "show") {

			@Override
			protected void tailed(SubcommandInvocation inv) {
				var l = channels.get(inv.event.getAuthor().getId());
				if (l == null || l.isEmpty())
					inv.event.getChannel().sendMessage("You don't have any private channels.").queue();
				else {
					long tt = 0;
					String res = "Your channels: " + JavaTools.printInEnglish(l.iterator(), true);
					for (var v : l)
						tt += v.cost();
					inv.event.getChannel()
							.sendMessage(res + "\nTotal Hourly Tax: " + Utilities.format(BigInteger.valueOf(tt)))
							.queue();
				}
			}
		};

		new Subcommand("create", "buy", "new") {

			@Override
			protected void tailed(SubcommandInvocation inv) {

				if (inv.args.length == 0)
					inv.event.getChannel()
							.sendMessage(inv.event.getAuthor().getAsMention() + " you need to provide a channel name.")
							.queue();
				else if (inv.args.length != 1)
					inv.event.getChannel()
							.sendMessage(
									inv.event.getAuthor().getAsMention() + " this command only takes one argument.")
							.queue();
				else if (inv.args[0].length() > 100)
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
							+ " discord channel names cannot be more than 100 characters long.").queue();
				else {
					if (clover.getEconomy().hasAccount(inv.event.getAuthor().getId())) {
						UserAccount acc = clover.getEconomy().getAccount(inv.event.getAuthor().getId());
						var g = inv.event.getGuild();
						String cg = clover.getEconomy().getServer(g.getId()).getPCCategory();
						if (cg == null) {
							inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
									+ ", this server does not have a private channel category set up (talk to an admin to set that up so you can use private channels).");
							return;
						}
						if (acc.withdraw(25000)) {
							var cat = g.getCategoryById(cg);
							var tc = cat.createTextChannel(inv.args[0]).complete();
							tc.createPermissionOverride(g.getMember(inv.event.getAuthor()))
									.setAllow(Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_ADD_REACTION,
											Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS,
//											Permission.MANAGE_WEBHOOKS,
											// Webhook permissions (currently) have too many security exploits,
											// including some in the bot (can fake claim buttons, for example).
											Permission.MESSAGE_HISTORY, Permission.MESSAGE_MANAGE,
											Permission.MESSAGE_TTS, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ)
									.queue();
							// No mentioning everyone because staff also have access + owner can add random
							// users.
							EconomyUser owner = acc.getOwner();
							System.out.println("TC ID: " + tc.getId());
							PrivateChannel pc = new PrivateChannel(
									new File(clover.getRandStorage(PRIVATE_CHANNEL_FILE_NAMESPACE + "/channels"),
											tc.getId() + ".txt"),
									clover, tc.getId(), owner.getUserID());
							pc.save();
							putChannel(pc);
							inv.event.getChannel().sendMessage("You now have a new private channel!").queue();
							return;
						}
					}
					inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention() + " you need "
							+ Utilities.format(BigInteger.valueOf(25000)) + " to create your own private channel.")
							.queue();
				}
			}
		};

	}

	@Override
	protected void tailed(CommandInvocation inv) {
		inv.event.getChannel().sendMessage(inv.event.getAuthor().getAsMention()
				+ " unknown subcommand. Options: `list`, `buy`, `invite`, and `delete`.").queue();
	}

}
