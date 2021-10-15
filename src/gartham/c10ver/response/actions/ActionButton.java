package gartham.c10ver.response.actions;

import java.util.function.Consumer;

import net.dv8tion.jda.api.interactions.components.Component;

public class ActionButton extends Action {
	private Component component;
	private String emoji;

	public String getEmoji() {
		return emoji;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public Component getComponent() {
		return component;
	}

	public ActionButton(Component component) {
		this.component = component;
	}

	public ActionButton(Consumer<ActionInvocation> action, Component component) {
		super(action);
		this.component = component;
	}

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

}
