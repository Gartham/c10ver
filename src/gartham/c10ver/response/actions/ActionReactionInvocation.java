package gartham.c10ver.response.actions;

import gartham.c10ver.Clover;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ActionReactionInvocation<R extends ActionReaction> {
	private final MessageReactionAddEvent event;
	private final ActionMessage<R, ?> message;
	private final Clover clover;

	public ActionReactionInvocation(MessageReactionAddEvent event, ActionMessage<R, ?> message, Clover clover) {
		this.event = event;
		this.message = message;
		this.clover = clover;
	}

	public ActionMessage<R, ?> getMessage() {
		return message;
	}

	public MessageReactionAddEvent getEvent() {
		return event;
	}

	public Clover getClover() {
		return clover;
	}
}
