package gartham.c10ver.response.actions;

import java.util.function.Consumer;

public class NamedActionReaction extends ActionReaction {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NamedActionReaction(String emoji, Consumer<ActionInvocation> action, String name) {
		super(emoji, action);
		this.name = name;
	}

}
