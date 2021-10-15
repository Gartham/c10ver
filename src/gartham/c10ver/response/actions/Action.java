package gartham.c10ver.response.actions;

import java.util.function.Consumer;

public class Action {

	protected Consumer<ActionInvocation> action;

	public void setAction(Consumer<ActionInvocation> action) {
		this.action = action;
	}

	public Action() {
	}

	public Action(Consumer<ActionInvocation> action) {
		this.action = action;
	}

	public Consumer<ActionInvocation> getAction() {
		return action;
	}

}