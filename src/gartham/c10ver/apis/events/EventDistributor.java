package gartham.c10ver.apis.events;

import java.util.HashMap;
import java.util.Map;

import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class EventDistributor implements EventListener {

	private final Map<Class<?>, InputProcessor<?>> inputProcessors = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <E extends GenericEvent> InputProcessor<E> getProcessor(Class<E> eventType) {
		if (!inputProcessors.containsKey(eventType))
			inputProcessors.put(eventType, new InputProcessor<>());
		return (InputProcessor<E>) inputProcessors.get(eventType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(GenericEvent event) {
		// TODO Run all input processors. InputProcessors are handled from lower classes
		// to higher classes. If a lower handles, the uppers don't get to.
		for (Class<?> c = event.getClass(); GenericEvent.class.isAssignableFrom(c); c = c.getSuperclass())
			if (inputProcessors.containsKey(c))
				if (((InputProcessor<GenericEvent>) inputProcessors.get(c)).runInputHandlers(event))
					return;// runInputHandlers returns true if the input has already been handled.
	}

}
