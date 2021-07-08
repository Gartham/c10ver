package gartham.c10ver.commands.consumers;

import java.time.Instant;

import org.alixia.javalibrary.strings.StringTools;
import org.alixia.javalibrary.util.Box;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

public interface MessageReactionInputConsumer<E extends GenericMessageReactionEvent> extends InputConsumer<E> {

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

	default MessageReactionInputConsumer<E> withTTL(long millis) {
		return expires(Instant.now().plusMillis(millis));
	}

	default MessageReactionInputConsumer<E> withActivityTTL(long millis) {
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

	default MessageReactionInputConsumer<E> withActivityTTL(long millis, Runnable action) {
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

	default MessageReactionInputConsumer<E> expires(Instant ts) {
		return (a, b, c) -> {
			if (Instant.now().isAfter(ts))
				b.removeInputConsumer(c);
			else
				return consume(a, b, c);
			return false;
		};
	}

	default MessageReactionInputConsumer<E> withTTL(long millis, Runnable action) {
		return expires(Instant.now().plusMillis(millis), action);
	}

	default MessageReactionInputConsumer<E> anyOf(String... emojis) {
		return (event, processor, consumer) -> {
			for (var s : emojis)
				if (event.getReactionEmote().getEmoji().equals(s))
					return consume(event, processor, consumer);
			return false;
		};
	}

	/**
	 * Returns an {@link InputConsumer} that invokes this {@link InputConsumer}
	 * unless the current time is after the specified {@link Instant}. If the
	 * current time is after, this {@link InputConsumer} is removed and then the
	 * specified action is invoked. In experimation, <code>false</code> is returned
	 * by the {@link InputConsumer} so that it will not affect the processing of the
	 * processor.
	 * 
	 * @param ts     The timestamp at which this {@link InputConsumer} "expires."
	 * @param action The action to invoke after this {@link InputConsumer} has been
	 *               removed from its processor.
	 * @return The new {@link InputConsumer}.
	 */
	default MessageReactionInputConsumer<E> expires(Instant ts, Runnable action) {
		return (a, b, c) -> {
			if (Instant.now().isAfter(ts)) {
				b.removeInputConsumer(c);
				action.run();
			} else
				return consume(a, b, c);
			return false;
		};
	}

	@Override
	default MessageReactionInputConsumer<E> oneTime() {
		return (event, processor, consumer) -> {
			var res = consume(event, processor, consumer);
			if (res)
				processor.removeInputConsumer(consumer);
			return res;
		};
	}

}
