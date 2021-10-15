package gartham.c10ver.response.actions;

import java.util.function.Consumer;

public class Action {

	protected Consumer<ActionReactionInvocation> action;

	public void setAction(Consumer<ActionReactionInvocation> action) {
		this.action = action;
	}

	public Action() {
	}

	public Action(Consumer<ActionReactionInvocation> action) {
		this.action = action;
	}

	public Consumer<ActionReactionInvocation> getAction() {
		return action;
	}

}