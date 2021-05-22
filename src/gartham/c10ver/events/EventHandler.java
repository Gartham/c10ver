package gartham.c10ver.events;

import static gartham.c10ver.events.InfoPopup.tip;
import static gartham.c10ver.utils.Utilities.format;
import static java.math.BigInteger.valueOf;

import java.math.BigInteger;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.economy.User;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.crates.NormalCrate;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import zeale.applicationss.notesss.utilities.generators.Generator;

public class EventHandler implements EventListener {

	private final Clover clover;
	private final InputProcessor<MessageReceivedEvent> messageProcessor = new InputProcessor<>();
	private final InputProcessor<MessageReactionAddEvent> reactionAdditionProcessor = new InputProcessor<>();
	private final Generator<InfoPopup> infoPopupGenerator = Generator.arrayLoop(tip(
			"You can get daily, weekly, and monthly rewards with the commands: `~daily`, `~weekly`, and `~monthly` respectively!"),
			tip("Every time you send a message in #general, there's a small chance you'll stumble upon some loot."),
			tip("You can open crates using the `open crate` command! Just type `~open crate crate-type`."),
			tip("You can pay other users using the `pay` command!"),
			tip("Eating food will give you a temporary multiplier. You can eat food with `~use food-name`."));
	private final InviteTracker inviteTracker = new InviteTracker(this);

	public Clover getClover() {
		return clover;
	}

	public InputProcessor<MessageReceivedEvent> getMessageProcessor() {
		return messageProcessor;
	}

	public InputProcessor<MessageReactionAddEvent> getReactionAdditionProcessor() {
		return reactionAdditionProcessor;
	}

	public EventHandler(Clover clover) {
		this.clover = clover;
	}

	public void initialize() {
		inviteTracker.initialize();
	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent) {
			var mre = (MessageReceivedEvent) event;

			var cmd = false;
			if (!messageProcessor.runInputHandlers(mre)) {
				var commandInvoc = clover.getCommandParser().parse(mre.getMessage().getContentRaw(), mre);
				if (commandInvoc != null) {
					clover.getCommandProcessor().run(commandInvoc);
					cmd = true;// TODO Go off of run method.
				}
			} else
				cmd = true;

			if (mre.isFromGuild() && clover.getEconomy().hasServer(mre.getGuild().getId())) {
				User user = clover.getEconomy().getUser(mre.getAuthor().getId());
				user.incrementMessageCount();
				if (user.getMessageCount().getLowestSetBit() >= 4)// Save every 16 messages.
					user.save();
				if (!mre.getAuthor().isBot()) {
					BigInteger rewards = switch (user.getMessageCount().toString()) {
					case "10" -> valueOf(50);
					case "50" -> valueOf(100);
					case "100" -> valueOf(250);// TODO Handle spam channel.
					case "200" -> valueOf(300);
					case "250" -> valueOf(400);
					case "300" -> valueOf(450);
					case "400" -> valueOf(500);
					case "500" -> valueOf(750);
					case "750" -> valueOf(1000);
					case "1000" -> valueOf(1_500);
					case "2000" -> valueOf(2_500);
					case "2500" -> valueOf(3_000);
					case "3000" -> valueOf(3_000);
					case "4000" -> valueOf(5_000);
					case "5000" -> valueOf(10_000);
					case "10000" -> valueOf(25_000);
					case "15000" -> valueOf(40_000);
					case "25000" -> valueOf(50_000);
					case "50000" -> valueOf(100_000);
					case "75000" -> valueOf(100_000);
					case "100000" -> valueOf(100_000);
					case "250000" -> valueOf(500_000);
					case "500000" -> valueOf(1_000_000);
					case "1000000" -> valueOf(25_000_000);
					default -> null;
					};
					if (rewards != null) {
						var mult = user.calcMultiplier(mre.getGuild());
						var amt = user.rewardAndSave(rewards, mult);
						mre.getChannel()
								.sendMessage(mre.getAuthor().getAsMention() + " congratulations, you just reached "
										+ user.getMessageCount() + " messages! You've earned: "
										+ Utilities.listRewards(amt, mult))
								.queue();
					} else {
						var serv = clover.getEconomy().getServer(mre.getGuild().getId());
						if (serv.isGeneral(mre.getChannel())) {
							if (Math.random() < 0.02) {
								var mult = user.calcMultiplier(mre.getGuild());
								BigInteger rawrew = BigInteger.valueOf((long) (Math.random() * 20 + 40));
								user.rewardAndSave(rawrew, mult);
								mre.getChannel()
										.sendMessage(mre.getAuthor().getAsMention()
												+ ", you found some cloves sitting on the ground.\n"
												+ Utilities.listRewards(rawrew, mult) + "\nTotal Cloves: "
												+ format(user.getAccount().getBalance()))
										.queue();
							} else if (cmd && Math.random() < 0.08)
								infoPopupGenerator.next().show(mre);
							else if (Math.random() < 0.01) {
								if (Math.random() < 0.2) {
									NormalCrate crate = new NormalCrate();
									user.getInventory().add(crate).save();
									mre.getChannel()
											.sendMessage(mre.getAuthor().getAsMention()
													+ " you look hungry... for a loot crate! (Acquired `1`x "
													+ crate.getIcon() + crate.getEffectiveName() + ".)")
											.queue();
								} else {
									BigInteger count = BigInteger.valueOf((long) (Math.random() * 3 + 1));
									Sandwich item = new Sandwich();
									user.getInventory().add(new ItemBunch<>(item, count)).save();
									mre.getChannel()
											.sendMessage(mre.getAuthor().getAsMention()
													+ " you look hungry. Have some sandwiches! (Acquired `" + count
													+ "`x " + item.getIcon() + item.getEffectiveName() + ".)")
											.queue();

								}
							}
						}
					}
				}
			}

		} else if (event instanceof MessageReactionAddEvent)
			reactionAdditionProcessor.runInputHandlers((MessageReactionAddEvent) event);
		else if (event instanceof GuildMemberJoinEvent) {
			var ge = (GuildMemberJoinEvent) event;
			var u = inviteTracker.calcUser(ge);
			if (u == null) {
				System.err.println(u);
				return;
			} else if (u.isFake())
				return;
			var inviter = clover.getEconomy().getAccount(u.getId());
			var joinee = clover.getEconomy().getUser(ge.getUser().getId());
			if (joinee.getJoinedGuilds().contains(ge.getGuild().getId()))
				return;

			inviter.deposit(500);
			inviter.save();
			joinee.getAccount().deposit(500);
			joinee.getAccount().save();
			joinee.getJoinedGuilds().add(ge.getGuild().getId());

			if (clover.getEconomy().hasServer(ge.getGuild().getId())) {
				var g = clover.getEconomy().getServer(ge.getGuild().getId());
				if (g.getGeneralChannel() != null) {
					var gen = ge.getGuild().getTextChannelById(g.getGeneralChannel());
					if (gen != null)
						gen.sendMessage(ge.getUser().getAsMention() + " welcome to the server. ^w^\nYou and "
								+ inviter.getUser().getUser().getAsMention() + " both received "
								+ Utilities.format(BigInteger.valueOf(500))).queue();
				}
			}

		} else if (event instanceof GuildInviteCreateEvent)
			inviteTracker.inviteCreated((GuildInviteCreateEvent) event);
		else if (event instanceof GuildInviteDeleteEvent)
			inviteTracker.inviteDeleted((GuildInviteDeleteEvent) event);
	}

}
