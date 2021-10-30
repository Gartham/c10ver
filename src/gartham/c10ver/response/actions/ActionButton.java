package gartham.c10ver.response.actions;

import java.util.function.Consumer;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.Component;

public class ActionButton {
	protected Consumer<ActionButtonInvocation> action;

	public Consumer<ActionButtonInvocation> getAction() {
		return action;
	}

	public void setAction(Consumer<ActionButtonInvocation> action) {
		this.action = action;
	}

	private Message ntr;

	public ActionButton setNontargetReply(String message) {
		ntr = new MessageBuilder(message).build();
		return this;
	}

	public ActionButton setNontargetReply(Message msg) {
		ntr = msg;
		return this;
	}

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

	public ActionButton(Consumer<ActionButtonInvocation> action, Component component) {
		this.action = action;
		this.component = component;
	}

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

	public Message getNontargetReply() {
		return ntr;
	}

}
