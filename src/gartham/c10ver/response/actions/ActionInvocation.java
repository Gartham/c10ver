package gartham.c10ver.response.actions;

import gartham.c10ver.Clover;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ActionInvocation {
	private final MessageReactionAddEvent event;
	private final ActionMessage message;
	private final Clover clover;

	public ActionInvocation(MessageReactionAddEvent event, ActionMessage message, Clover clover) {
		this.event = event;
		this.message = message;
		this.clover = clover;
	}

	public ActionMessage getMessage() {
		return message;
	}

	public MessageReactionAddEvent getEvent() {
		return event;
	}

	public Clover getClover() {
		return clover;
	}
}
