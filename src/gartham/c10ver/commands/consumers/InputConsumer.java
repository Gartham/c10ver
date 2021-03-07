package gartham.c10ver.commands.consumers;

import java.time.Instant;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.events.EventHandler;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface InputConsumer<E extends GenericEvent> {

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
	 *                     {@link #consume(E, EventHandler, InputConsumer)} method
	 *                     on this {@link InputConsumer}.
	 * @param consumer     This {@link InputConsumer}. Can be used to refer to this
	 *                     {@link InputConsumer} from inside a lambda expression.
	 * @return <code>true</code> if the event should be consumed, <code>false</code>
	 *         otherwise.
	 */
	boolean consume(E event, InputProcessor<? extends E> eventHandler, InputConsumer<E> consumer);

	default boolean consume(E event, InputProcessor<? extends E> eventHandler) {
		return consume(event, eventHandler, this);
	}

	default InputConsumer<E> withTTL(long millis) {
		return expires(Instant.now().plusMillis(millis));
	}

	default InputConsumer<E> expires(Instant ts) {
		return (a, b, c) -> {
			if (Instant.now().isAfter(ts))
				b.removeInputConsumer(this);
			else
				return consume(a, b, c);
			return false;
		};
	}

}
