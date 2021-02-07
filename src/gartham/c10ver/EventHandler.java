package gartham.c10ver;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class EventHandler implements EventListener {

	private final Clover clover;

	public EventHandler(Clover clover) {
		this.clover = clover;
	}

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof MessageReceivedEvent) {
			var mre = (MessageReceivedEvent) event;
			var commandInvoc = clover.getCommandParser().parse(mre.getMessage().getContentRaw(), mre);
			if (commandInvoc == null)
				return;
			clover.getCommandProcessor().run(commandInvoc);
		}
	}

}
