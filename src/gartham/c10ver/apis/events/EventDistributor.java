package gartham.c10ver.apis.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.alixia.javalibrary.util.Listmap;

import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class EventDistributor implements EventListener {

	private final Map<Class<?>, InputProcessor<?>> inputProcessors = new HashMap<>();
	private final Listmap<Class<? extends GenericEvent>, Consumer<? super GenericEvent>, ArrayList<Consumer<? super GenericEvent>>> eventHandlers = Listmap
			.arrayListMap(), eventResponders = Listmap.arrayListMap();

	public void registerHandler(Class<? extends GenericEvent> key, Consumer<? super GenericEvent> element) {
		eventHandlers.putElement(key, element);
	}

	public void registerResponder(Class<? extends GenericEvent> key, Consumer<? super GenericEvent> element) {
		eventResponders.putElement(key, element);
	}

	public void unregisterHandler(Class<? extends GenericEvent> key, Consumer<? super GenericEvent> element) {
		eventHandlers.removeElement(key, element);
	}

	public void unregisterResponder(Class<? extends GenericEvent> key, Consumer<? super GenericEvent> element) {
		eventResponders.removeElement(key, element);
	}

	public boolean isHandlerRegistered(Class<? extends GenericEvent> key, Consumer<? super GenericEvent> element) {
		return eventHandlers.containsElement(key, element);
	}

	public boolean isResponderRegistered(Class<? extends GenericEvent> key, Consumer<? super GenericEvent> element) {
		return eventResponders.containsElement(key, element);
	}

	public boolean isRegistered(Class<? extends GenericEvent> key, Consumer<? super GenericEvent> element) {
		return isHandlerRegistered(key, element) || isResponderRegistered(key, element);
	}

	@SuppressWarnings("unchecked")
	public <E extends GenericEvent> InputProcessor<E> getProcessor(Class<E> eventType) {
		if (!inputProcessors.containsKey(eventType))
			inputProcessors.put(eventType, new InputProcessor<>());
		return (InputProcessor<E>) inputProcessors.get(eventType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(GenericEvent event) {

		// Run all event handlers initially.
		for (Class<?> c = event.getClass(); GenericEvent.class.isAssignableFrom(c); c = c.getSuperclass())
			if (eventHandlers.containsKey(c))
				for (var v : eventHandlers.get(c))
					v.accept(event);

		// InputProcessors are handled from lower classes to higher classes. If a lower
		// handles, the uppers don't get to.
		for (Class<?> c = event.getClass(); GenericEvent.class.isAssignableFrom(c); c = c.getSuperclass())
			if (inputProcessors.containsKey(c))
				if (((InputProcessor<GenericEvent>) inputProcessors.get(c)).runInputHandlers(event))
					return;// runInputHandlers returns true if the input has already been handled.

		// Run all event responders if input processors do not block them.
		for (Class<?> c = event.getClass(); GenericEvent.class.isAssignableFrom(c); c = c.getSuperclass())
			if (eventResponders.containsKey(c))
				for (var v : eventResponders.get(c))
					v.accept(event);

	}

}
