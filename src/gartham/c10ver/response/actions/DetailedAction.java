package gartham.c10ver.response.actions;

import java.util.function.Consumer;

public class DetailedAction extends ActionReaction {
	private String details;

	public DetailedAction(String emoji, String name, String details, Consumer<ActionInvocation> action) {
		super(emoji, name, action);
		this.details = details;
	}

	public DetailedAction(String name, String details, Consumer<ActionInvocation> action) {
		this(null, name, details, action);
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
