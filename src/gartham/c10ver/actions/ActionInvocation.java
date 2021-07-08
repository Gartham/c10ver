package gartham.c10ver.actions;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ActionInvocation {
	private final MessageReactionAddEvent event;
	private final ActionMessage message;

	public ActionInvocation(MessageReactionAddEvent event, ActionMessage message) {
		this.event = event;
		this.message = message;
	}

	public ActionMessage getMessage() {
		return message;
	}

	public MessageReactionAddEvent getEvent() {
		return event;
	}
}
