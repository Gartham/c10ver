package gartham.c10ver.response.actions;

import java.util.function.Consumer;

import net.dv8tion.jda.api.interactions.components.Component;

public class DetailedActionButton extends NamedActionButton {
	private String details;

	public DetailedActionButton(Component component, String name, String emoji, String details) {
		super(component, name, emoji);
		this.details = details;
	}

	public DetailedActionButton(Consumer<ActionButtonInvocation> action, Component component, String name, String emoji,
			String details) {
		super(action, component, name, emoji);
		this.details = details;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
