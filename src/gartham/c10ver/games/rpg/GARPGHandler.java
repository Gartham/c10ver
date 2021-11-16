package gartham.c10ver.games.rpg;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.economy.GARPGState;
import gartham.c10ver.games.rpg.creatures.Creature;
import gartham.c10ver.games.rpg.creatures.Nymph;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

/**
 * Entry point for GARPG handling. Handles discord events and RPG logic.
 * 
 * @author Gartham
 *
 */
public class GARPGHandler implements EventListener {

	private final InputProcessor<MessageReactionAddEvent> reactionProcessor = new InputProcessor<>();
	private final InputProcessor<ButtonClickEvent> buttonProcessor = new InputProcessor<>();
	private final Creature friend = new Nymph();

	private final Clover clover;

	public GARPGHandler(Clover clover) {
		this.clover = clover;
	}

	private GARPGState resolve(GuildMessageReceivedEvent event) {
		return clover.getEconomy().getUser(event.getAuthor().getId()).getGarpgState();
	}

	private void onEvent(GuildMessageReceivedEvent event) {
		var rpgstate = resolve(event);
		if (rpgstate.isActive()) {

		} else {
			GarmonUtils.sendAsCreature(friend, "Hi there.", event.getChannel());
		}
	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof GuildMessageReceivedEvent) {
			GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;
			if (!checkChannel(e.getGuild().getId(), e.getChannel().getId(), e.getAuthor()))
				return;
			if (e.isWebhookMessage())
				return;
			onEvent(e);
		} else if (event instanceof MessageReactionAddEvent) {
			MessageReactionAddEvent e = (MessageReactionAddEvent) event;
			if (!e.isFromGuild())
				return;
			if (!checkChannel(e.getGuild().getId(), e.getChannel().getId(), e.retrieveUser().complete()))
				return;
			reactionProcessor.runInputHandlers(e);
		} else if (event instanceof ButtonClickEvent) {
			ButtonClickEvent e = (ButtonClickEvent) event;
			if (!e.isFromGuild())
				return;
			if (!checkChannel(e.getGuild().getId(), e.getChannel().getId(), e.getUser()))
				return;
			buttonProcessor.runInputHandlers(e);
		}
	}

	public boolean checkChannel(String serverID, String channelID, User author) {
		if (!clover.getEconomy().hasServer(serverID))
			return false;
		if (author == null) {
			System.err.println("Error in GaRPG channel, event causer is null.");
			return false;
		}
		if (author.isBot() || author.isSystem())
			return false;
		return channelID.equals(clover.getEconomy().getServer(serverID).getRPGChannel());
	}

}
