package gartham.c10ver.actions;

import java.util.function.Consumer;

public interface Action extends Consumer<ActionInvocation> {

	public static Action msg(Action... actionMessage) {
		return msg(new SimpleActionMessage(actionMessage));
	}

	public static Action msg(Consumer<ActionInvocation> action, Action... actionMessage) {
		return msg(new SimpleActionMessage(actionMessage), action);
	}

	public static Action msg(ActionMessage msg) {
		return t -> msg.send(t.getClover(), t.getEvent().getChannel());
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
	public static Action msg(ActionMessage msg, Consumer<ActionInvocation> action) {
		return t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel());
		};
	}
}
