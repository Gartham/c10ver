package gartham.c10ver.response.actions;

import java.util.function.Consumer;

import gartham.c10ver.response.menus.MenuMessage;

public class ActionReaction {

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

	public void setAction(Consumer<ActionInvocation> action) {
		this.action = action;
	}

	private String emoji;
	private Consumer<ActionInvocation> action;

	public ActionReaction(String emoji, Consumer<ActionInvocation> action) {
		this.emoji = emoji;
		this.action = action;
	}

	public ActionReaction(Consumer<ActionInvocation> action) {
		this(null, action);
	}

	public Consumer<ActionInvocation> getAction() {
		return action;
	}

	public String getEmoji() {
		return emoji;
	}

	public void accept(ActionInvocation invoc) {
		this.action.accept(invoc);
	}

	public static ActionReaction msg(MenuMessage<? extends ActionReaction> msg) {
		return new ActionReaction(null, t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser()));
	}

	public static ActionReaction msg(String emoji, MenuMessage<? extends ActionReaction> msg) {
		return new ActionReaction(emoji, t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser()));
	}

	public static Consumer<ActionInvocation> actionMessageAction(ActionReaction... actionMessages) {
		return actionMessageAction(new SimpleMenuMessage<>(actionMessages));
	}

	public static Consumer<ActionInvocation> actionMessageAction(MenuMessage<? extends ActionReaction> msg) {
		return t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
	}

	public static Consumer<ActionInvocation> actionMessageAction(MenuMessage<?> msg,
			Consumer<ActionInvocation> action) {
		return t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
		};
	}

	/**
	 * Returns a new {@link ActionReaction} that executes the provided {@link Consumer}
	 * action and then sends the provided {@link ActionMessage}.
	 * 
	 * @param msg    The {@link ActionMessage} to send once this {@link ActionReaction} is
	 *               executed.
	 * @param action The code to run when this {@link ActionReaction} is executed.
	 * @return A new {@link ActionReaction} wrapping the provided objects.
	 */
	public static ActionReaction msg(MenuMessage<?> msg, Consumer<ActionInvocation> action) {
		return msg(null, msg, action);
	}

	public static ActionReaction msg(String emoji, MenuMessage<?> msg, Consumer<ActionInvocation> action) {
		return new ActionReaction(emoji, t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
		});
	}
}