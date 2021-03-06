package gartham.c10ver.commands;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.commands.consumers.InputConsumer;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InputProcessor<E extends GenericEvent> {
	private final List<InputConsumer<? super E>> consumers = new ArrayList<>(100), toRemove = new ArrayList<>(20);

	public synchronized void registerInputConsumer(InputConsumer<? super E> ic) {
		consumers.add(ic);
	}

	public synchronized void removeInputConsumer(InputConsumer<? super E> ic) {
		consumers.remove(ic);
	}

	/**
	 * <p>
	 * Schedules the specified {@link InputConsumer} to be removed at the end of the
	 * next {@link InputConsumer} run cycle.
	 * </p>
	 * <p>
	 * Whenever {@link InputConsumer}s are being checked during the handling of a
	 * {@link MessageReceivedEvent}, the {@link Iterable} that this class uses to
	 * store them is being iterated over to access all of the
	 * {@link InputConsumer}s, so none can be removed while they're running.
	 * Problematically, one may be considered "dead" and will need to be removed
	 * although not consume the event, so it can be scheduled for removal by this
	 * method. At the end of a {@link #runInputHandlers(GenericEvent)}'s execution, all of the
	 * {@link InputConsumer}s that have been scheduled to be removed until, will be
	 * removed from the {@link Iterable}.
	 * </p>
	 * 
	 * @param ic The {@link InputConsumer} to schedule for removal.
	 */
	public synchronized void scheduleForRemoval(InputConsumer<? super E> ic) {
		toRemove.add(ic);
	}

	public synchronized boolean runInputHandlers(E mre) {
		boolean res;
		DEC: {
			for (var ic : consumers)
				if (ic.consume(mre, this)) {
					res = true;
					break DEC;
				}
			res = false;
		}
		for (var ic : toRemove)
			consumers.remove(ic);
		toRemove.clear();
		return res;
	}
}
