package gartham.c10ver.response.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageReactionInputConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

//If #reactions contains any reactions, handler must not be empty. If
// pageHandler is not empty, handler must not be empty.
// Page handling delegates to #handler if #pageHandler is empty.

// Page indexing starts at 0.
// TODO Clear Documentation.
public class ReactionBookMenu {
	private static final String RIGHT_ALL = "\u23ED";
	private static final String LEFT_ALL = "\u23EE";
	private static final String RIGHT_ONE = "\u25B6";
	private static final String LEFT_ONE = "\u25C0";

	private Consumer<String> handler;
	private Consumer<Integer> pageHandler;
	/**
	 * Represents the maximum page of this {@link ReactionBookMenu}. This is used to
	 * track what the right "edge" button does (specifically, how far to the right
	 * it goes).
	 */
	private int maxPage = -1;

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public void setHandler(Consumer<String> handler) {
		this.handler = handler;
	}

	public void setProcessor(InputProcessor<MessageReactionAddEvent> processor) {
		this.processor = processor;
	}

	private User target;
	private InputProcessor<MessageReactionAddEvent> processor;
	private boolean edgeButtons;
	private final List<String> reactions = new ArrayList<>();

	public ReactionBookMenu(InputProcessor<MessageReactionAddEvent> processor) {
		this.processor = processor;
	}

	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}

	public boolean isEdgeButtons() {
		return edgeButtons;
	}

	public void setEdgeButtons(boolean edgeButtons) {
		this.edgeButtons = edgeButtons;
	}

	public Consumer<String> getHandler() {
		return handler;
	}

	public InputProcessor<MessageReactionAddEvent> getProcessor() {
		return processor;
	}

	public List<String> getReactions() {
		return reactions;
	}

	public void add(String reaction) {
		reactions.add(reaction);
	}

	public void remove(String reaction) {
		reactions.remove(reaction);
	}

	public void removeTarget() {
		target = null;
	}

	public Consumer<Integer> getPageHandler() {
		return pageHandler;
	}

	public void setPageHandler(Consumer<Integer> pageHandler) {
		this.pageHandler = pageHandler;
	}

	/**
	 * Attaches this {@link ReactionBookMenu} (in its current state) to the provided
	 * {@link Message}. This {@link ReactionBookMenu} can be safely modified and
	 * used on a different {@link Message}. Modifications to this
	 * {@link ReactionBookMenu} that are subsequent to an invocation of this method
	 * will not affect that invocation of this method.
	 * 
	 * @param message The {@link Message} to attach the {@link ReactionBookMenu} to.
	 */
	public void attach(Message message) {
		final List<String> x = new ArrayList<>(reactions.size() + (edgeButtons ? 4 : 2));
		x.add(0, LEFT_ONE);
		x.add(RIGHT_ONE);
		if (edgeButtons) {
			x.add(0, LEFT_ALL);
			x.add(RIGHT_ALL);
		}
		for (var s : reactions)
			x.add(normalizeEmoji(s));

		// TODO Returned object should allow the updating of all four of these
		// properties. Remember that a null target means no target, but such is not yet
		// implemented!
		var h = handler;
		var ph = pageHandler;
		var max = maxPage;
		var targ = target;
		// The only feature that will not be modifiable in the returned object is the
		// reaction emojis themselves. These will have had to have been sent with the
		// message itself.
		if (!x.isEmpty()) {
			for (var s : x)
				message.addReaction(normalizeEmoji(s)).queue();

			processor.registerInputConsumer((new MessageReactionInputConsumer<MessageReactionAddEvent>() {

				// TODO Returned object should also include a "setPage(int)" method.
				int page = 0;

				@Override
				public boolean consume(MessageReactionAddEvent event,
						InputProcessor<? extends MessageReactionAddEvent> p,
						InputConsumer<MessageReactionAddEvent> consumer) {

					String e = event.getReactionEmote().getName();
					if (ph != null) {
						switch (e) { // If it is a page button, handle, but do not unregister the consumer.
						case LEFT_ALL:
							if (page != 0)
								ph.accept(page = 0);
							message.removeReaction(e, targ).queue();
							return true;
						case LEFT_ONE:
							if (page != 0)
								ph.accept(--page);
							message.removeReaction(e, targ).queue();
							return true;
						case RIGHT_ONE:
							if (page != max)
								ph.accept(++page);
							message.removeReaction(e, targ).queue();
							return true;
						default:
							if (page != max)
								ph.accept(page = max);
							message.removeReaction(e, targ).queue();
							return true;
						}
					}
					// Handle any non-page-handle clicks.
					for (var s : x)
						if (s.equalsIgnoreCase(e)) {
							h.accept(s);
							// TODO In returned object, have dynamically updatable field to indicate if
							// should be removed on next click.
							// For now, instead treat as "one time."
							// TODO Returned object should also allow at-will unregistration.
							p.removeInputConsumer(consumer);
							return true;
						}
					return false;
				}
			}).filter(target, message));
		}
	}

	public static String normalizeEmoji(String emoji) {
		if ((emoji = emoji.toLowerCase()).startsWith("a:"))
			if (emoji.length() == 2)
				throw new IllegalArgumentException();
			else
				emoji.substring(2);
		return emoji;
	}

}
