package gartham.c10ver.events;

import java.util.HashMap;
import java.util.Map;

import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class EventHandler implements EventListener {

	private final Map<Class<?>, InputProcessor<?>> inputProcessors = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <E extends GenericEvent> InputProcessor<E> getProcessor(Class<E> eventType) {
		if (!inputProcessors.containsKey(eventType))
			inputProcessors.put(eventType, new InputProcessor<>());
		return (InputProcessor<E>) inputProcessors.get(eventType);
	}

	public InputProcessor<MessageReceivedEvent> getMessageProcessor() {
		return getProcessor(MessageReceivedEvent.class);
	}

	public InputProcessor<MessageReactionAddEvent> getReactionAdditionProcessor() {
		return getProcessor(MessageReactionAddEvent.class);
	}

	public InputProcessor<ButtonClickEvent> getButtonClickProcessor() {
		return getProcessor(ButtonClickEvent.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(GenericEvent event) {
		for (Class<?> c = event.getClass(); GenericEvent.class.isAssignableFrom(c); c = c.getSuperclass())
			if (inputProcessors.containsKey(c))
				((InputProcessor<GenericEvent>) inputProcessors.get(c)).runInputHandlers(event);
	}

}
