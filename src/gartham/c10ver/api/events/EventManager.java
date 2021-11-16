package gartham.c10ver.api.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

/**
 * <p>
 * Provides an abstraction on top of JDA's {@link EventListener} for better
 * modularized handling of all types of events.
 * </p>
 * <h3>Functionality</h3>
 * <p>
 * This class provides two primary ways of handling events:
 * <ul>
 * <li><b>Standard</b></li>
 * <li><b>Consumer-based</b></li>
 * </ul>
 * </p>
 * <h4>Standard</h4>
 * <p>
 * In Standard event handling, {@link Consumer} objects are registered with an
 * <em>event type</em>. A registered {@link Consumer} is invoked whenever events
 * of the type it is registered with occur. Standard event handling allows
 * filtration of events
 * </p>
 * <p>
 * Multiple {@link Consumer}s can be registered for the same event type, and the
 * same {@link Consumer} can be registered multiple times (it will be invoked
 * once per time it is registered whenever the respective event occurs).
 * <h4>Consumer-based</h4>
 * 
 * @author Gartham
 *
 */
public class EventManager {

	private final Map<Class<?>, List<Consumer<?>>> standardEventHandlers = new HashMap<>();

	public <E extends GenericEvent> void register(Class<E> eventType, Consumer<? super E> handler) {
		if (!standardEventHandlers.containsKey(eventType))
			standardEventHandlers.put(eventType, new ArrayList<>());
		standardEventHandlers.get(eventType).add(handler);
	}

	public <E extends GenericEvent> void unregister(Class<E> eventType, Consumer<? super E> handler) {
		if (standardEventHandlers.containsKey(eventType)) {
			List<Consumer<?>> handlers = standardEventHandlers.get(eventType);
			handlers.remove(handler);
			if (handlers.isEmpty())
				standardEventHandlers.remove(eventType);
		}
	}

	private final Map<Class<?>, InputProcessor<?>> inputProcessors = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <E extends GenericEvent> InputProcessor<E> getProcessor(Class<E> eventType) {
		if (!inputProcessors.containsKey(eventType))
			inputProcessors.put(eventType, new InputProcessor<>());
		return (InputProcessor<E>) inputProcessors.get(eventType);
	}

}
