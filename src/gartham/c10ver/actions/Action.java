package gartham.c10ver.actions;

import java.util.function.Consumer;

public class Action {

	private final String emoji, description;
	private final Consumer<ActionInvocation> action;

	public Action(String emoji, String description, Consumer<ActionInvocation> action) {
		this.emoji = emoji;
		this.description = description;
		this.action = action;
	}

	public Action(String description, Consumer<ActionInvocation> action) {
		this(null, description, action);
	}

	public String getDescription() {
		return description;
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

	public static Action msg(String desc, Action... actionMessage) {
		return msg(desc, new SimpleActionMessage(desc, actionMessage));
	}

	public static Action msg(String desc, Consumer<ActionInvocation> action, Action... actionMessage) {
		return msg(desc, new SimpleActionMessage(desc, actionMessage), action);
	}

	public static Action msg(String desc, ActionMessage msg) {
		return new Action(null, desc, t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser()));
	}

	public static Action msg(String emoji, String desc, Action... actionMessage) {
		return msg(emoji, desc, new SimpleActionMessage(desc, actionMessage));
	}

	public static Action msg(String emoji, String desc, Consumer<ActionInvocation> action, Action... actionMessage) {
		return msg(emoji, desc, new SimpleActionMessage(desc, actionMessage), action);
	}

	public static Action msg(String emoji, String desc, ActionMessage msg) {
		return new Action(emoji, desc, t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser()));
	}

	/**
	 * Returns a new {@link Action} that executes the provided {@link Consumer}
	 * action and then sends the provided {@link ActionMessage}.
	 * 
	 * @param msg    The {@link ActionMessage} to send once this {@link Action} is
	 *               executed.
	 * @param action The code to run when this {@link Action} is executed.
	 * @return A new {@link Action} wrapping the provided objects.
	 */
	public static Action msg(String desc, ActionMessage msg, Consumer<ActionInvocation> action) {
		return msg(null, desc, msg, action);
	}

	public static Action msg(String emoji, String desc, ActionMessage msg, Consumer<ActionInvocation> action) {
		return new Action(emoji, desc, t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
		});
	}
}
