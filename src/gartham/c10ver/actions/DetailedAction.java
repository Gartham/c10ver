package gartham.c10ver.actions;

import java.util.function.Consumer;

public class DetailedAction extends Action {
	private String details;

	public DetailedAction(String emoji, String name, String details, Consumer<ActionInvocation> action) {
		super(emoji, name, action);
		this.details = details;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
