package gartham.c10ver.response.actions;

import gartham.c10ver.Clover;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class ActionButtonInvocation<B extends ActionButton> {
	private final ButtonClickEvent event;
	private final ActionMessage<?, B> message;
	private final Clover clover;

	public ActionButtonInvocation(ButtonClickEvent event, ActionMessage<?, B> message, Clover clover) {
		this.event = event;
		this.message = message;
		this.clover = clover;
	}

	public ActionMessage<?, B> getMessage() {
		return message;
	}

	public ButtonClickEvent getEvent() {
		return event;
	}

	public Clover getClover() {
		return clover;
	}
}
