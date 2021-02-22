package gartham.c10ver.events;

import static gartham.c10ver.utils.Utilities.format;

import java.math.BigInteger;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.economy.User;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import zeale.applicationss.notesss.utilities.generators.Generator;
import static gartham.c10ver.events.InfoPopup.*;

public class EventHandler implements EventListener {

	private final Clover clover;
	private final InputProcessor<MessageReceivedEvent> messageProcessor = new InputProcessor<>(this);
	private final InputProcessor<MessageReactionAddEvent> reactionAdditionProcessor = new InputProcessor<>(this);
	private final Generator<InfoPopup> infoPopupGenerator = Generator.arrayLoop(tip(
			"You can get daily, weekly, and monthly rewards with the commands: `~daily`, `~weekly`, and `~monthly` respectively!"),
			tip("Every time you send a message in #general, there's a small chance you'll stumble upon some loot."));

	public InputProcessor<MessageReceivedEvent> getMessageProcessor() {
		return messageProcessor;
	}

	public InputProcessor<MessageReactionAddEvent> getReactionAdditionProcessor() {
		return reactionAdditionProcessor;
	}

	public EventHandler(Clover clover) {
		this.clover = clover;
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

			if (clover.getEconomy().hasServer(mre.getGuild().getId())) {
				User user = clover.getEconomy().getUser(mre.getAuthor().getId());
				user.incrementMessageCount();
				if (user.getMessageCount().getLowestSetBit() >= 4)// Save every 16 messages.
					user.save();
				if (!mre.getAuthor().isBot()) {
					var serv = clover.getEconomy().getServer(mre.getGuild().getId());
					if (serv.isGeneral(mre.getChannel())) {
						if (Math.random() < 0.02) {
							var mult = user.calcMultiplier(mre.getGuild());
							BigInteger rawrew = BigInteger.valueOf((long) (Math.random() * 20 + 40));
							user.rewardAndSave(rawrew, mult);
							mre.getChannel()
									.sendMessage(mre.getAuthor().getAsMention()
											+ ", you found some coins sitting on the ground.\n"
											+ Utilities.listRewards(rawrew, mult) + "\nTotal Cloves: "
											+ format(user.getAccount().getBalance()))
									.queue();
						} else if (cmd && Math.random() < 0.08)
							infoPopupGenerator.next().show(mre);
					}
				}
			}

		} else if (event instanceof MessageReactionAddEvent) {
			var mrae = (MessageReactionAddEvent) event;
			reactionAdditionProcessor.runInputHandlers(mrae);
		}
	}

}
