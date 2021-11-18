package gartham.c10ver.apis.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.alixia.javalibrary.util.Listmap;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.EventListener;

/**
 * <h1>Event Distributor</h1>
 * <p>
 * Handles distribution of JDA events. Provides more complex event logic between
 * registered event handlers. See below for disambiguation.
 * </p>
 * <h2>API</h2>
 * <p>
 * This class provides three primary ways of handling JDA events.
 * <ol>
 * <li><b>Event Handlers</b> receive all incoming events that are an
 * <code style="color:purple;"><b>instanceof</b></code> the event type they are
 * registered with. They are the first of the three types to see each event.
 * <ul>
 * <li>Always receive events <i>first</i> (before the other two types of event
 * handling).</li>
 * <li>Suitable for handlers that should always get to handle the events they
 * listen for. Examples include code that monitors every {@link GuildJoinEvent},
 * or code that logs {@link MessageDeleteEvent message deletions} for moderation
 * purposes. These processes should not be "interrupted" because of other
 * handlers' behavior.</li>
 * </ul>
 * </li>
 * <li><b>Input Consumers</b> receive events of the type they are registered
 * with and are able to <i>consume</i> the event, causing it to not be handled
 * further by any other type of handler (be it an Event Handler, other Input
 * Processor, or Event Responder). Input Consumers receive events <i>second</i>
 * (after Event Handlers are all finished). See the {@link InputConsumer} API
 * documentation for more details.
 * <ul>
 * <li>Receive events second.</li>
 * <li>Suitable for handlers that need to (temporarily) handle events before
 * Event Responders can (and may need to block Event Responders from handling).
 * A good example of this is a command confirmation:<br>
 * <figure style="display:block;"><img src="doc-files/InputConsumerExample.png"
 * width= 400px><figcaption>An Input Consumer being used to allow a user to
 * confirm an
 * operation.</figcaption></figure><figure style="display:block;"><img src=
 * "doc-files/InputConsumerBlocksCommand.png" width=400px><figcaption>Input
 * Consumer blocking the <code>help</code> command (by consuming the event
 * before it reaches any Event Responders).</figcaption></figure>Until an Input
 * Consumer (or many Consumers) stop consuming a type of event (or types of
 * events), Event Responders registered to receive those will not see those
 * events. This makes it possible to register commands as Event Responders and
 * then have some commands register a special "yes/no" Input Consumer <i>on the
 * fly</i>, to prompt the user or similar.</li>
 * </ul>
 * <li><b>Event Responders</b> are similar to Event Handlers, but receive
 * (extant) events after Event Handlers and Input Consumers are both done
 * processing them.
 * <ul>
 * <li>Receive events last.</li>
 * <li>Events only received if not already consumed (by Input Consumers).</li>
 * <li>Suitable for commands and other event-handling code that should be
 * blocked e.g. by prompts created by other commands.</li>
 * </ul>
 * </li>
 * </ol>
 * 
 * All three types provide the ability to filter handled events based on event
 * type on a per-handler basis.
 * 
 * <pre>
 * <code>EventDistributor <span style=
 * "color:lightbrown;">dist</span> = ...;<span style=
 * "color:darkgreen;">// Initialize EventDistributor</span>
 * 
 * dist.registerHandler({@link MessageDeleteEvent}.<span style=
"color:purple;"><b>class</b></span>, <span style=
"color:purple;"><b>new</b></span> MessageDeletionLogger();<span style=
"color:darkgreen">// Register an Event Handler that will log all message deletions.
 // It only gets executed when a {@link MessageDeleteEvent} occurs.</span></code>
 * </pre>
 * </p>
 * 
 * @author Gartham
 *
 */
public class EventDistributor implements EventListener {

	/**
	 * Creates a new {@link Consumer} based off the provided {@link EventListener}.
	 * All events provided to the {@link Consumer} are simply forwarded to the
	 * {@link EventListener}.
	 * 
	 * @param <E>      The type of the {@link Consumer}.
	 * @param listener The {@link EventListener} to receive the events.
	 * @return The new {@link Consumer}.
	 */
	public static <E extends GenericEvent> Consumer<? extends E> consumer(EventListener listener) {
		return listener::onEvent;
	}

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
