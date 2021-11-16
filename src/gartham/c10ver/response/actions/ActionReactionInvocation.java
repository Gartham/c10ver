package gartham.c10ver.response.actions;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ActionReactionInvocation {
	private final MessageReactionAddEvent event;
	private final ActionMessage<?, ?> message;
	private final InputProcessor<MessageReactionAddEvent> reactionProcessor;
	private final InputProcessor<ButtonClickEvent> buttonClickProcessor;

	public ActionReactionInvocation(MessageReactionAddEvent event, ActionMessage<?, ?> message, Clover clover) {
		this(event, message, clover.getEventHandler().getReactionAdditionProcessor(),
				clover.getEventHandler().getButtonClickProcessor());
	}

	public ActionReactionInvocation(MessageReactionAddEvent event, ActionMessage<?, ?> message,
			InputProcessor<MessageReactionAddEvent> reactionProcessor,
			InputProcessor<ButtonClickEvent> buttonProcessor) {
		this.event = event;
		this.message = message;
		this.reactionProcessor = reactionProcessor;
		this.buttonClickProcessor = buttonProcessor;
	}

	public ActionMessage<?, ?> getMessage() {
		return message;
	}

	public MessageReactionAddEvent getEvent() {
		return event;
	}

	public InputProcessor<MessageReactionAddEvent> getReactionProcessor() {
		return reactionProcessor;
	}

	public InputProcessor<ButtonClickEvent> getButtonClickProcessor() {
		return buttonClickProcessor;
	}

}
