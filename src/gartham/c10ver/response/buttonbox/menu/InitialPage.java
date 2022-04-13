package gartham.c10ver.response.buttonbox.menu;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public interface InitialPage extends Page {
	MessageAction prepareMessage(MessageChannel channel);
}
