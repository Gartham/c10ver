package gartham.c10ver.commands.consumers;

import gartham.c10ver.events.EventHandler;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface InputConsumer {
	/**
	 * Is tasked with optionally consuming the specified event so that command
	 * handlers and later-registered {@link InputConsumer}s don't consume it
	 * instead. This method is provided a {@link MessageReceivedEvent} as long as it
	 * is registered. If it returns <code>true</code>, it the specified
	 * {@link MessageReceivedEvent} is considered "handled" and is not propagated to
	 * other {@link InputConsumer}s or any command handlers. If this method returns
	 * <code>false</code>, the event is propagated, and is treated as if this
	 * {@link InputConsumer} "didn't match" the input.
	 * 
	 * @param event        The event to handle.
	 * @param eventHandler The {@link EventHandler} that is executing the
	 *                     {@link #consume(MessageReceivedEvent, EventHandler, InputConsumer)}
	 *                     method on this {@link InputConsumer}.
	 * @param consumer     This {@link InputConsumer}. Can be used to refer to this
	 *                     {@link InputConsumer} from inside a lambda expression.
	 * @return <code>true</code> if the event should be consumed, <code>false</code>
	 *         otherwise.
	 */
	boolean consume(MessageReceivedEvent event, EventHandler eventHandler, InputConsumer consumer);

	/**
	 * Returns an {@link InputConsumer} which calls this {@link InputConsumer} if
	 * the author of the message has the specified ID. Otherwise, it returns
	 * <code>false</code>.
	 * 
	 * @param userID
	 * @return
	 */
	default InputConsumer filterUser(String userID) {
		return (event, eventHandler, consumer) -> {
			if (event.getAuthor().getId().equals(userID))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default InputConsumer filterChannel(String channelID) {
		return (event, eventHandler, consumer) -> {
			if (event.getChannel().getId().equals(channelID))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default InputConsumer filter(String userID, String channelID) {
		return (event, eventHandler, consumer) -> {
			if (event.getChannel().getId().equals(channelID) && event.getAuthor().getId().equals(userID))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default InputConsumer filter(User user, MessageChannel channel) {
		return (event, eventHandler, consumer) -> {
			if (event.getChannel().getId().equals(channel.getId()) && event.getAuthor().getId().equals(user.getId()))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}
}
