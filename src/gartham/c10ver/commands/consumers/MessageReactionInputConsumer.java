package gartham.c10ver.commands.consumers;

import org.alixia.javalibrary.strings.StringTools;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

public interface MessageReactionInputConsumer<E extends GenericMessageReactionEvent> extends InputConsumer<E> {

	/**
	 * Returns an {@link InputConsumer} which calls this {@link InputConsumer} if
	 * the author of the message has the specified ID. Otherwise, it returns
	 * <code>false</code>.
	 * 
	 * @param userID
	 * @return
	 */
	default MessageReactionInputConsumer<E> filterUser(String userID) {
		return (event, eventHandler,
				consumer) -> event.getUser().getId().equals(userID) ? consume(event, eventHandler, consumer) : false;
	}

	default MessageReactionInputConsumer<E> filterChannel(String channelID) {
		return (event, eventHandler, consumer) -> event.getChannel().getId().equals(channelID)
				? consume(event, eventHandler, consumer)
				: false;
	}

	default MessageReactionInputConsumer<E> filterUser(String... userIDs) {
		return (event, eventHandler, consumer) -> StringTools.equalsAny(event.getUser().getId(), userIDs)
				? consume(event, eventHandler, consumer)
				: false;
	}

	default MessageReactionInputConsumer<E> filterChannel(String... channelIDs) {
		return (event, eventHandler, consumer) -> StringTools.equalsAny(event.getChannel().getId(), channelIDs)
				? consume(event, eventHandler, consumer)
				: false;
	}

	default MessageReactionInputConsumer<E> filter(String userID, String channelID) {
		return (event, eventHandler, consumer) -> event.getChannel().getId().equals(channelID)
				&& event.getUser().getId().equals(userID) ? consume(event, eventHandler, consumer) : false;
	}

	default MessageReactionInputConsumer<E> filter(User user, MessageChannel channel) {
		return (event, eventHandler,
				consumer) -> event.getChannel().getId().equals(channel.getId())
						&& event.getUser().getId().equals(user.getId()) ? consume(event, eventHandler, consumer)
								: false;
	}

}
