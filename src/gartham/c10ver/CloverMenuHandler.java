package gartham.c10ver;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageInputConsumer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CloverMenuHandler implements MessageInputConsumer {
	private final Clover clover;

	public CloverMenuHandler(Clover clover) {
		this.clover = clover;
	}

	@Override
	public boolean consume(MessageReceivedEvent event, InputProcessor<? extends MessageReceivedEvent> processor,
			InputConsumer<MessageReceivedEvent> consumer) {

		return false;
	}

}
