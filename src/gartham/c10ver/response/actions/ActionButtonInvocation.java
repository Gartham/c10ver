package gartham.c10ver.response.actions;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ActionButtonInvocation {
	private final ButtonClickEvent event;
	private final ActionMessage<?, ?> message;
	private final InputProcessor<MessageReactionAddEvent> reactionProcessor;
	private final InputProcessor<ButtonClickEvent> buttonClickProcessor;

	public ActionButtonInvocation(ButtonClickEvent event, ActionMessage<?, ?> message, Clover clover) {
		this(event, message, clover.getEventHandler().getReactionAdditionProcessor(),
				clover.getEventHandler().getButtonClickProcessor());
	}

	public ActionButtonInvocation(ButtonClickEvent event, ActionMessage<?, ?> message,
			InputProcessor<MessageReactionAddEvent> reactionProcessor,
			InputProcessor<ButtonClickEvent> buttonClickProcessor) {
		this.event = event;
		this.message = message;
		this.reactionProcessor = reactionProcessor;
		this.buttonClickProcessor = buttonClickProcessor;
	}

	public ActionMessage<?, ?> getMessage() {
		return message;
	}

	public ButtonClickEvent getEvent() {
		return event;
	}

	public InputProcessor<MessageReactionAddEvent> getReactionProcessor() {
		return reactionProcessor;
	}

	public InputProcessor<ButtonClickEvent> getButtonClickProcessor() {
		return buttonClickProcessor;
	}

}
