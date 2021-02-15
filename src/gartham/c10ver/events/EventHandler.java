package gartham.c10ver.events;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class EventHandler implements EventListener {

	private final Clover clover;
	private final InputProcessor<MessageReceivedEvent> messageProcessor = new InputProcessor<>(this);
	private final InputProcessor<MessageReactionAddEvent> reactionAdditionProcessor = new InputProcessor<>(this);

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
			if (messageProcessor.runInputHandlers(mre))
				return;
			var commandInvoc = clover.getCommandParser().parse(mre.getMessage().getContentRaw(), mre);
			if (commandInvoc == null)
				return;
			clover.getCommandProcessor().run(commandInvoc);
		} else if (event instanceof MessageReactionAddEvent) {
			var mrae = (MessageReactionAddEvent) event;
			reactionAdditionProcessor.runInputHandlers(mrae);
		}
	}

}
