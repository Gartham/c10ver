package gartham.c10ver.response.actions.v2;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ActionMessage {
	private final Message message;
	private final Consumer<String> handler;
	private final InputProcessor<MessageReactionAddEvent> processor;

	private final Set<String> reactions;

	private User target;

	public ActionMessage(Message message, Consumer<String> handler, InputProcessor<MessageReactionAddEvent> processor,
			String... reactions) {
		this.message = message;
		this.handler = handler;
		this.processor = processor;
		this.reactions = new HashSet<>();
		for (var s : reactions)
			this.reactions.add(s);
	}

	public ActionMessage(Message message, Consumer<String> handler, InputProcessor<MessageReactionAddEvent> processor,
			Collection<String> reactions) {
		this.message = message;
		this.handler = handler;
		this.processor = processor;
		this.reactions = new HashSet<>(reactions);
	}

	public ActionMessage(Message message, Consumer<String> handler, InputProcessor<MessageReactionAddEvent> processor,
			Iterator<String> reactions) {
		this.message = message;
		this.handler = handler;
		this.processor = processor;
		this.reactions = new HashSet<>();
		while (reactions.hasNext())
			this.reactions.add(reactions.next());
	}

	public User getTarget() {
		return target;
	}

	public ActionMessage setTarget(User target) {
		this.target = target;
		return this;
	}

	public Message getMessage() {
		return message;
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

}
