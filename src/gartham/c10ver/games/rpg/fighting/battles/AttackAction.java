package gartham.c10ver.games.rpg.fighting.battles;

import java.util.function.Consumer;

import gartham.c10ver.actions.Action;
import gartham.c10ver.actions.ActionInvocation;

public class AttackAction extends Action {
	private final String description;
	private final Consumer<ActionInvocation> handler;

	public AttackAction(String emoji, String name, Consumer<ActionInvocation> action, String description,
			Consumer<ActionInvocation> handler) {
		super(emoji, name, action);
		this.description = description;
		this.handler = handler;
	}

	public AttackAction(String name, Consumer<ActionInvocation> action, String description,
			Consumer<ActionInvocation> handler) {
		this(null, name, action, description, handler);
	}

	public String getDescription() {
		return description;
	}

	public Consumer<ActionInvocation> getHandler() {
		return handler;
	}

}
