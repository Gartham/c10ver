package gartham.c10ver.response.menus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.MessageReactionInputConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionBookMenu {
	// If #reactions contains any reactions, handler must not be empty. If
	// pageHandler is not empty, handler must not be empty.
	// Page handling delegates to #handler if #pageHandler is empty.
	private Consumer<String> handler;
	private Consumer<Integer> pageHandler;

	public void setHandler(Consumer<String> handler) {
		this.handler = handler;
	}

	public void setProcessor(InputProcessor<MessageReactionAddEvent> processor) {
		this.processor = processor;
	}

	private User target;
	private InputProcessor<MessageReactionAddEvent> processor;
	private boolean edgeButtons;
	private final Set<String> reactions = new HashSet<>();

	public ReactionBookMenu(InputProcessor<MessageReactionAddEvent> processor) {
		this.processor = processor;
	}

	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}

	public boolean isEdgeButtons() {
		return edgeButtons;
	}

	public void setEdgeButtons(boolean edgeButtons) {
		this.edgeButtons = edgeButtons;
	}

	public Consumer<String> getHandler() {
		return handler;
	}

	public InputProcessor<MessageReactionAddEvent> getProcessor() {
		return processor;
	}

	public Set<String> getReactions() {
		return reactions;
	}

	public void add(String reaction) {
		reactions.add(reaction);
	}

	public void remove(String reaction) {
		reactions.remove(reaction);
	}

	public void removeTarget() {
		target = null;
	}

	public Consumer<Integer> getPageHandler() {
		return pageHandler;
	}

	public void setPageHandler(Consumer<Integer> pageHandler) {
		this.pageHandler = pageHandler;
	}

	/**
	 * Attaches this {@link ReactionBookMenu} (in its current state) to the provided
	 * {@link Message}. This {@link ReactionBookMenu} can be safely modified and
	 * used on a different {@link Message}. Modifications to this
	 * {@link ReactionBookMenu} that are subsequent to an invocation of this method
	 * will not affect that invocation of this method.
	 * 
	 * @param message The {@link Message} to attach the {@link ReactionBookMenu} to.
	 */
	public void attach(Message message) {
		final Set<String> x = new HashSet<>(reactions.size());
		for (var s : reactions)
			x.add(normalizeEmoji(s));
		var h = handler;
		if (!x.isEmpty()) {
			for (var s : x)
				message.addReaction(normalizeEmoji(s)).queue();

			processor.registerInputConsumer(
					((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, p, consumer) -> {
						if (event.getMessageIdLong() != message.getIdLong())
							return false;
						for (var s : x) {
							if (s.equalsIgnoreCase(event.getReactionEmote().getName())) {
								h.accept(s);
								return true;
							}
						}
						return false;
					}).filter(target, message).oneTime());
		}
	}

	public static String normalizeEmoji(String emoji) {
		if ((emoji = emoji.toLowerCase()).startsWith("a:"))
			if (emoji.length() == 2)
				throw new IllegalArgumentException();
			else
				emoji.substring(2);
		return emoji;
	}

}
