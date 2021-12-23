package gartham.c10ver.response.menus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionBookMenu {
	private Consumer<String> handler;

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

	public ReactionBookMenu(Consumer<String> handler, InputProcessor<MessageReactionAddEvent> processor) {
		this.handler = handler;
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

	public void attach(Message message) {
		// TODO
	}

}
