package gartham.c10ver.response.actions;

import java.util.function.Consumer;

public class DetailedActionReaction extends NamedActionReaction {
	private String details;

	public DetailedActionReaction(String emoji, String name, String details, Consumer<ActionReactionInvocation> action) {
		super(emoji, action, name);
		this.details = details;
	}

	public DetailedActionReaction(String name, String details, Consumer<ActionReactionInvocation> action) {
		this(null, name, details, action);
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
