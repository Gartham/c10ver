package gartham.c10ver.response.menus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
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

	public void attach(Message message) {
		// TODO
	}

}
