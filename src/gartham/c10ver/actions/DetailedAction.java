package gartham.c10ver.actions;

import java.util.function.Consumer;

public class DetailedAction extends Action {
	private final String details;

	public DetailedAction(String emoji, String name, Consumer<ActionInvocation> action, String details) {
		super(emoji, name, action);
		this.details = details;
	}

	public String getDetails() {
		return details;
	}

}
