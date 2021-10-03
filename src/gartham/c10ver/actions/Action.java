package gartham.c10ver.actions;

import java.util.function.Consumer;

/**
 * <p>
 * Represents an option in an {@link ActionMessage} menu. This class is a part
 * of the {@link ActionMessage} API which is used to show the user menus
 * ({@link ActionMessage}s). {@link Action}s contain the basic necessities of an
 * option, including the emoji that the user clicks to react, the name of an
 * action, as well as the actual operation to perform when a user reacts
 * (captured in {@link #action}). This class can be extended to provide
 * further/custom functionality. An example of this is {@link DetailedAction},
 * which allows specifying a description for an action.
 * </p>
 * 
 * @author Gartham
 *
 */
public class Action {

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAction(Consumer<ActionInvocation> action) {
		this.action = action;
	}

	private String emoji, name;
	private Consumer<ActionInvocation> action;

	public Action(String emoji, String name, Consumer<ActionInvocation> action) {
		this.emoji = emoji;
		this.name = name;
		this.action = action;
	}

	public Action(String name, Consumer<ActionInvocation> action) {
		this(null, name, action);
	}

	public String getName() {
		return name;
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

	public static Action msg(String name, ActionMessage<? extends Action> msg) {
		return new Action(null, name, t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser()));
	}

	public static Action msg(String emoji, String name, ActionMessage<? extends Action> msg) {
		return new Action(emoji, name, t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser()));
	}

	public static Consumer<ActionInvocation> actionMessageAction(Action... actionMessages) {
		return actionMessageAction(new SimpleActionMessage<>(actionMessages));
	}

	public static Consumer<ActionInvocation> actionMessageAction(ActionMessage<? extends Action> msg) {
		return t -> msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
	}

	public static Consumer<ActionInvocation> actionMessageAction(ActionMessage<?> msg,
			Consumer<ActionInvocation> action) {
		return t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
		};
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
	public static Action msg(String desc, ActionMessage<?> msg, Consumer<ActionInvocation> action) {
		return msg(null, desc, msg, action);
	}

	public static Action msg(String emoji, String desc, ActionMessage<?> msg, Consumer<ActionInvocation> action) {
		return new Action(emoji, desc, t -> {
			action.accept(t);
			msg.send(t.getClover(), t.getEvent().getChannel(), t.getEvent().getUser());
		});
	}
}
