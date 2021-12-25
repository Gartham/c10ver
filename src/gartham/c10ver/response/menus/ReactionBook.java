package gartham.c10ver.response.menus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.response.ResponseUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

//If #reactions contains any reactions, handler must not be empty. If
// pageHandler is not empty, handler must not be empty.
// Page handling delegates to #handler if #pageHandler is empty.

// Page indexing starts at 0.
// TODO Clear Documentation.
public class ReactionBook {
	private Consumer<String> handler;
	private Consumer<Integer> pageHandler;
	/**
	 * Represents the maximum page of this {@link ReactionBook}. This is used to
	 * track what the right "edge" button does (specifically, how far to the right
	 * it goes).
	 */
	private int maxPage = Integer.MAX_VALUE;

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

	public ReactionBook(InputProcessor<MessageReactionAddEvent> processor) {
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

	// TODO This class (and the surrounding class) are in desparate need of
	// documentation.
	public static final class ActiveReactionBook {
		private final List<String> reactions;// This will not be changeable.
		private final InputConsumer<MessageReactionAddEvent> inc;
		private final InputProcessor<MessageReactionAddEvent> processor;
		private Consumer<String> handler;
		private Consumer<Integer> pageHandler;
		private int maxPage;
		private User target;
		private final Message message;
		private int page;
		private boolean oneTime = true;

		private ActiveReactionBook(List<String> reactions, Consumer<String> handler, Consumer<Integer> pageHandler,
				int maxPage, User target, Message message, InputProcessor<MessageReactionAddEvent> processor) {
			this.reactions = reactions;
			this.handler = handler;
			this.pageHandler = pageHandler;
			this.maxPage = maxPage;
			this.target = target;

			inc = (event, p, consumer) -> {

				if (this.target != null && !this.target.equals(event.getUser()))
					return false;
				if (event.getMessageIdLong() != message.getIdLong())
					return false;

				String e = event.getReactionEmote().getName();
				if (this.pageHandler != null) {
					switch (e) { // If it is a page button, handle, but do not unregister the consumer.
					case ResponseUtils.LEFT_ALL:
						if (this.page > 0)
							this.pageHandler.accept(this.page = 0);
						message.removeReaction(e, event.getUser()).queue();
						return true;
					case ResponseUtils.LEFT_ONE:
						if (this.page > 0)
							this.pageHandler.accept(--this.page);
						message.removeReaction(e, event.getUser()).queue();
						return true;
					case ResponseUtils.RIGHT_ONE:
						if (this.page < this.maxPage)
							this.pageHandler.accept(++this.page);
						message.removeReaction(e, event.getUser()).queue();
						return true;
					case ResponseUtils.RIGHT_ALL:
						if (this.page < this.maxPage)
							this.pageHandler.accept(this.page = this.maxPage);
						message.removeReaction(e, event.getUser()).queue();
						return true;
					}
				}

				for (var s : this.reactions)
					if (s.equalsIgnoreCase(e)) {
						this.handler.accept(s);
						if (oneTime)
							p.removeInputConsumer(consumer);
						return true;
					}
				return false;
			};
			(this.processor = processor).registerInputConsumer(inc);
			this.message = message;

		}

		public void unregister() {
			processor.removeInputConsumer(inc);
		}

		public void register() {
			processor.registerInputConsumer(inc);
		}

		public Consumer<String> getHandler() {
			return handler;
		}

		public void setHandler(Consumer<String> handler) {
			this.handler = handler;
		}

		public Consumer<Integer> getPageHandler() {
			return pageHandler;
		}

		public void setPageHandler(Consumer<Integer> pageHandler) {
			this.pageHandler = pageHandler;
		}

		public int getMaxPage() {
			return maxPage;
		}

		/**
		 * Changes the maximum page that this book can be on. This does not invoke the
		 * {@link #getPageHandler() page handler} or the {@link #getHandler() reaction
		 * handler} at all, and may need a call to
		 * 
		 * @param maxPage The new maximum page that the user can paginate to.
		 */
		public void setMaxPage(int maxPage) {
			if (maxPage < 0)
				throw new IllegalArgumentException();
			if (maxPage < page)
				page = maxPage;
			this.maxPage = maxPage;
		}

		/**
		 * Invokes the {@link #getPageHandler() page handler}, if there is one, with the
		 * current value of {@link #page}. This is useful for cases where {@link #page}
		 * is updated by code utilizing this class, rather than by an actual reaction to
		 * the {@link ReactionBook} message object. In such cases, (as when calling
		 * {@link #setPage(int)}), the message will not automatically update (neither
		 * {@link #getPageHandler()} nor {@link #getHandler()} will automatically be
		 * called). In such cases, calling {@link #update()} after changes to
		 * {@link #page} will alleviate the issue.
		 */
		public void update() {
			if (pageHandler != null)
				pageHandler.accept(page);
		}

		public User getTarget() {
			return target;
		}

		public void setTarget(User target) {
			this.target = target;
		}

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			if (page > maxPage)
				throw new IllegalArgumentException();
			this.page = page;
		}

		public List<String> getReactions() {
			return Collections.unmodifiableList(reactions);
		}

		public Message getMessage() {
			return message;
		}

		public InputConsumer<MessageReactionAddEvent> getInputConsumer() {
			return inc;
		}

	}

	public ActiveReactionBook attach(Message message) {
		var res = new ActiveReactionBook(new ArrayList<>(reactions.size() + (edgeButtons ? 4 : 2)), handler,
				pageHandler, maxPage, target, message, processor);
		for (var s : reactions)
			res.reactions.add(ResponseUtils.normalizeEmoji(s));
		res.reactions.add(0, ResponseUtils.LEFT_ONE);
		res.reactions.add(ResponseUtils.RIGHT_ONE);
		if (edgeButtons) {
			res.reactions.add(0, ResponseUtils.LEFT_ALL);
			res.reactions.add(ResponseUtils.RIGHT_ALL);
		}

		if (!res.reactions.isEmpty())
			for (var s : res.reactions)
				message.addReaction(ResponseUtils.normalizeEmoji(s)).queue();
		return res;
	}

}
