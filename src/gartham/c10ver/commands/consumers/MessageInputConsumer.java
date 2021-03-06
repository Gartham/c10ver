package gartham.c10ver.commands.consumers;

import java.time.Instant;

import org.alixia.javalibrary.strings.StringTools;
import org.alixia.javalibrary.util.Box;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface MessageInputConsumer extends InputConsumer<MessageReceivedEvent> {
	/**
	 * Returns an {@link InputConsumer} which calls this {@link InputConsumer} if
	 * the author of the message has the specified ID. Otherwise, it returns
	 * <code>false</code>.
	 * 
	 * @param userID
	 * @return
	 */
	default MessageInputConsumer filterUser(String userID) {
		return (event, eventHandler, consumer) -> {
			if (event.getAuthor().getId().equals(userID))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default MessageInputConsumer filterChannel(String channelID) {
		return (event, eventHandler, consumer) -> {
			if (event.getChannel().getId().equals(channelID))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default MessageInputConsumer filterUser(String... userIDs) {
		return (event, eventHandler, consumer) -> {
			if (StringTools.equalsAny(event.getAuthor().getId(), userIDs))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default MessageInputConsumer filterChannel(String... channelIDs) {
		return (event, eventHandler, consumer) -> {
			if (StringTools.equalsAny(event.getChannel().getId(), channelIDs))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default MessageInputConsumer filter(String userID, String channelID) {
		return (event, eventHandler, consumer) -> {
			if (event.getChannel().getId().equals(channelID) && event.getAuthor().getId().equals(userID))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default MessageInputConsumer filter(User user, MessageChannel channel) {
		return (event, eventHandler, consumer) -> {
			if (event.getChannel().getId().equals(channel.getId()) && event.getAuthor().getId().equals(user.getId()))
				return consume(event, eventHandler, consumer);
			else
				return false;
		};
	}

	default MessageInputConsumer withTTL(long millis) {
		return expires(Instant.now().plusMillis(millis));
	}

	default MessageInputConsumer expires(Instant ts) {
		return (a, b, c) -> {
			if (Instant.now().isAfter(ts))
				b.removeInputConsumer(c);
			else
				return consume(a, b, c);
			return false;
		};
	}

	default MessageInputConsumer withTTL(long millis, Runnable action) {
		return expires(Instant.now().plusMillis(millis), action);
	}

	default MessageInputConsumer expires(Instant ts, Runnable action) {
		return (a, b, c) -> {
			if (Instant.now().isAfter(ts)) {
				b.removeInputConsumer(c);
				action.run();
			} else
				return consume(a, b, c);
			return false;
		};
	}

	default MessageInputConsumer withActivityTTL(long millis) {
		var inst = new Box<>(Instant.now().plusMillis(millis));
		return (a, b, c) -> {
			if (Instant.now().isAfter(inst.value))
				b.removeInputConsumer(c);
			else {
				inst.value = Instant.now().plusMillis(millis);
				return consume(a, b, c);
			}
			return false;
		};
	}

	default MessageInputConsumer withActivityTTL(long millis, Runnable action) {
		var inst = new Box<>(Instant.now().plusMillis(millis));
		return (a, b, c) -> {
			if (Instant.now().isAfter(inst.value)) {
				b.removeInputConsumer(c);
				action.run();
			} else {
				inst.value = Instant.now().plusMillis(millis);
				return consume(a, b, c);
			}
			return false;
		};
	}

	@Override
	default MessageInputConsumer oneTime() {
		return (event, processor, consumer) -> {
			var res = consume(event, processor, consumer);
			if (res)
				processor.removeInputConsumer(consumer);
			return res;
		};
	}

}
