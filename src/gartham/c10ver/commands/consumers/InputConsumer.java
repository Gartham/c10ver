package gartham.c10ver.commands.consumers;

import java.time.Instant;

import org.alixia.javalibrary.util.Box;

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
	 * @param event     The event to handle.
	 * @param processor The {@link EventHandler} that is executing the
	 *                  {@link #consume(E, EventHandler, InputConsumer)} method on
	 *                  this {@link InputConsumer}.
	 * @param consumer  This {@link InputConsumer}. Can be used to refer to this
	 *                  {@link InputConsumer} from inside a lambda expression.
	 * @return <code>true</code> if the event should be consumed, <code>false</code>
	 *         otherwise.
	 */
	boolean consume(E event, InputProcessor<? extends E> processor, InputConsumer<E> consumer);

	default boolean consume(E event, InputProcessor<? extends E> eventHandler) {
		return consume(event, eventHandler, this);
	}

	default InputConsumer<E> withTTL(long millis) {
		return expires(Instant.now().plusMillis(millis));
	}

	default InputConsumer<E> withActivityTTL(long millis) {
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

	default InputConsumer<E> withActivityTTL(long millis, Runnable action) {
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

	default InputConsumer<E> expires(Instant ts) {
		return (a, b, c) -> {
			if (Instant.now().isAfter(ts))
				b.removeInputConsumer(c);
			else
				return consume(a, b, c);
			return false;
		};
	}

	default InputConsumer<E> withTTL(long millis, Runnable action) {
		return expires(Instant.now().plusMillis(millis), action);
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
	default InputConsumer<E> expires(Instant ts, Runnable action) {
		return (a, b, c) -> {
			if (Instant.now().isAfter(ts)) {
				b.removeInputConsumer(c);
				action.run();
			} else
				return consume(a, b, c);
			return false;
		};
	}

	default InputConsumer<E> oneTime() {
		return (event, processor, consumer) -> {
			var res = consume(event, processor, consumer);
			if (res)
				processor.removeInputConsumer(consumer);
			return res;
		};
	}

}
