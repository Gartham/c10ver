package gartham.c10ver.response.actions;

import java.util.function.Consumer;

import net.dv8tion.jda.api.interactions.components.Component;

public class NamedActionButton extends ActionButton {

	public NamedActionButton(Consumer<ActionButtonInvocation> action, Component component, String name, String emoji) {
		super(action, component);
		this.name = name;
		this.emoji = emoji;
	}

	public NamedActionButton(Component component, String name, String emoji) {
		super(component);
		this.name = name;
		this.emoji = emoji;
	}

	private String name, emoji;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmoji() {
		return emoji;
	}

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

}
