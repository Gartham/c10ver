package gartham.c10ver.games.rpg;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.economy.GARPGState;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

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

	public void onEvent(GuildMessageReceivedEvent event) {
		var rpgstate = resolve(event);
		if (rpgstate.isActive()) {

		} else {

		}
	}

	@Override
	public void onEvent(GenericEvent event) {
		// TODO Auto-generated method stub

	}

}
