package gartham.c10ver.actions;

import java.util.function.Consumer;

public class Action {

	private final String description;
	private final Consumer<ActionInvocation> action;

	public Action(String description, Consumer<ActionInvocation> action) {
		this.description = description;
		this.action = action;
	}

	public String getDescription() {
		return description;
	}

	public Consumer<ActionInvocation> getAction() {
		return action;
	}

	public void accept(ActionInvocation invoc) {
		this.action.accept(invoc);
	}

	public static Action msg(String desc, Action... actionMessage) {
		return msg(desc, new SimpleActionMessage(actionMessage));
	}

	public static Action msg(String desc, Consumer<ActionInvocation> action, Action... actionMessage) {
		return msg(desc, new SimpleActionMessage(actionMessage), action);
	}

	public static Action msg(String desc, ActionMessage msg) {
		return new Action(desc, t -> msg.send(t.getClover(), t.getEvent().getChannel()));
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
		return new Action(desc, t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel());
		});
	}
}
