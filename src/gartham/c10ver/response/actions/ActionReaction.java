package gartham.c10ver.response.actions;

import java.util.function.Consumer;

import gartham.c10ver.response.menus.MenuMessage;
import gartham.c10ver.response.menus.NameMenuMessage;

public class ActionReaction extends Action {

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

	private String emoji;

	public ActionReaction(String emoji, Consumer<ActionReactionInvocation> action) {
		this.emoji = emoji;
		this.action = action;
	}

	public ActionReaction(Consumer<ActionReactionInvocation> action) {
		this(null, action);
	}

	public String getEmoji() {
		return emoji;
	}

	public void accept(ActionReactionInvocation invoc) {
		this.action.accept(invoc);
	}

	public static ActionReaction msg(MenuMessage<? extends ActionReaction, ? extends ActionButton> msg) {
		return new ActionReaction(null,
				t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser()));
	}

	public static ActionReaction msg(String emoji, MenuMessage<? extends ActionReaction, ? extends ActionButton> msg) {
		return new ActionReaction(emoji,
				t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser()));
	}

	public static Consumer<ActionReactionInvocation> actionMessageAction(NamedActionReaction... actionMessages) {
		return actionMessageAction(new NameMenuMessage<>(new ActionMessage<>(actionMessages)));
	}

	public static Consumer<ActionReactionInvocation> actionMessageAction(
			MenuMessage<? extends ActionReaction, ? extends ActionButton> msg) {
		return t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
	}

	public static Consumer<ActionReactionInvocation> actionMessageAction(MenuMessage<?, ?> msg,
			Consumer<ActionReactionInvocation> action) {
		return t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
		};
	}

	/**
	 * Returns a new {@link ActionReaction} that executes the provided
	 * {@link Consumer} action and then sends the provided {@link ActionMessage}.
	 * 
	 * @param msg    The {@link ActionMessage} to send once this
	 *               {@link ActionReaction} is executed.
	 * @param action The code to run when this {@link ActionReaction} is executed.
	 * @return A new {@link ActionReaction} wrapping the provided objects.
	 */
	public static ActionReaction msg(MenuMessage<?, ?> msg, Consumer<ActionReactionInvocation> action) {
		return msg(null, msg, action);
	}

	public static ActionReaction msg(String emoji, MenuMessage<?, ?> msg, Consumer<ActionReactionInvocation> action) {
		return new ActionReaction(emoji, t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
		});
	}
}
