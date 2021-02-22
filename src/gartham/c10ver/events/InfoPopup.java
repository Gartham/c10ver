package gartham.c10ver.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface InfoPopup {
	void show(MessageReceivedEvent event);

	static InfoPopup of(String text) {
		return event -> event.getChannel().sendMessage(text).queue();
	}

}
