package gartham.c10ver.games.rpg;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.economy.GARPGState;
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

		}
	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof GuildMessageReceivedEvent) {
			GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;
			if (!checkChannel(e.getGuild().getId(), e.getChannel().getId()))
				return;
			onEvent(e);
		} else if (event instanceof MessageReactionAddEvent) {
			MessageReactionAddEvent e = (MessageReactionAddEvent) event;
			if (!e.isFromGuild())
				return;
			if (!checkChannel(e.getGuild().getId(), e.getChannel().getId()))
				return;
			reactionProcessor.runInputHandlers(e);
		} else if (event instanceof ButtonClickEvent) {
			ButtonClickEvent e = (ButtonClickEvent) event;
			if (!e.isFromGuild())
				return;
			if (!checkChannel(e.getGuild().getId(), e.getChannel().getId()))
				return;
			buttonProcessor.runInputHandlers(e);
		}
	}

	public boolean checkChannel(String serverID, String channelID) {
		if (!clover.getEconomy().hasServer(serverID))
			return false;
		return channelID.equals(clover.getEconomy().getServer(serverID).getRPGChannel());
	}

}
