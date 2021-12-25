package gartham.c10ver.response.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.response.ResponseUtils;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class ButtonBook {
	// TODO This class (and the surrounding class) are in desparate need of
	// documentation.
	public static final class ActiveButtonBook {
		private Consumer<ButtonClickEvent> handler;
		private final InputConsumer<ButtonClickEvent> inc;
		private int maxPage;
		private final Message message;
		private boolean oneTime = true;
		private int page;
		private BiConsumer<Integer, ButtonClickEvent> pageHandler;
		private final InputProcessor<ButtonClickEvent> processor;
		private User target;

		private ActiveButtonBook(Consumer<ButtonClickEvent> handler, BiConsumer<Integer, ButtonClickEvent> pageHandler,
				int maxPage, User target, Message message, InputProcessor<ButtonClickEvent> processor) {
			this.handler = handler;
			this.pageHandler = pageHandler;
			this.maxPage = maxPage;
			this.target = target;

			inc = (event, p, consumer) -> {

				if (this.target != null && !this.target.equals(event.getUser()))
					return false;
				if (event.getMessageIdLong() != message.getIdLong())
					return false;

				String e = event.getComponentId();
				if (this.pageHandler != null) {
					switch (e) { // If it is a page button, handle, but do not unregister the consumer.
					case "left-all":
						if (this.page > 0)
							this.pageHandler.accept(this.page = 0, event);
						return true;
					case "left-one":
						if (this.page > 0)
							this.pageHandler.accept(--this.page, event);
						return true;
					case "right-one":
						if (this.page < this.maxPage)
							this.pageHandler.accept(++this.page, event);
						return true;
					case "right-all":
						if (this.page < this.maxPage)
							this.pageHandler.accept(this.page = this.maxPage, event);
						return true;
					}
				}

				this.handler.accept(event);
				if (oneTime)
					p.removeInputConsumer(consumer);
				return true;
			};
			(this.processor = processor).registerInputConsumer(inc);
			this.message = message;

		}

		public Consumer<ButtonClickEvent> getHandler() {
			return handler;
		}

		public InputConsumer<ButtonClickEvent> getInputConsumer() {
			return inc;
		}

		public int getMaxPage() {
			return maxPage;
		}

		public Message getMessage() {
			return message;
		}

		public int getPage() {
			return page;
		}

		public BiConsumer<Integer, ButtonClickEvent> getPageHandler() {
			return pageHandler;
		}

		public User getTarget() {
			return target;
		}

		public void register() {
			processor.registerInputConsumer(inc);
		}

		public void setHandler(Consumer<ButtonClickEvent> handler) {
			this.handler = handler;
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

		public void setPage(int page) {
			if (page > maxPage)
				throw new IllegalArgumentException();
			this.page = page;
		}

		public void setPageHandler(BiConsumer<Integer, ButtonClickEvent> pageHandler) {
			this.pageHandler = pageHandler;
		}

		public void setTarget(User target) {
			this.target = target;
		}

		public void unregister() {
			processor.removeInputConsumer(inc);
		}

		/**
		 * Invokes the {@link #getPageHandler() page handler}, if there is one, with the
		 * current value of {@link #page} and the value <code>null</code> for the
		 * provided event. This is useful for cases where {@link #page} is updated by
		 * code utilizing this class, rather than by an actual reaction to the
		 * {@link ReactionBook} message object, but should only be used when the
		 * {@link #pageHandler} is equipped to handle input with the <code>null</code>
		 * event. In such cases, (as when calling {@link #setPage(int)}), the message
		 * will not automatically update (neither {@link #getPageHandler()} nor
		 * {@link #getHandler()} will automatically be called). In such cases, calling
		 * {@link #update()} after changes to {@link #page} will alleviate the issue.
		 */
		public void update() {
			if (pageHandler != null)
				pageHandler.accept(page, null);
		}

	}

	private final List<Button> buttons = new ArrayList<>();
	private boolean edgeButtons;
	private Consumer<ButtonClickEvent> handler;

	private int maxPage = Integer.MAX_VALUE;

	private BiConsumer<Integer, ButtonClickEvent> pageHandler;

	private final InputProcessor<ButtonClickEvent> processor;
	private User target;

	public ButtonBook(InputProcessor<ButtonClickEvent> processor) {
		this.processor = processor;
	}

	public void add(Button button) {
		buttons.add(button);
	}

	public ActiveButtonBook attachAndSend(MessageAction msg) {
		List<Button> buttons = new ArrayList<>(this.buttons);
		buttons.add(0, Button.primary("left-one", Emoji.fromMarkdown(ResponseUtils.LEFT_ONE)));
		buttons.add(Button.primary("right-one", Emoji.fromMarkdown(ResponseUtils.RIGHT_ONE)));
		if (edgeButtons) {
			buttons.add(0, Button.primary("left-all", Emoji.fromMarkdown(ResponseUtils.LEFT_ALL)));
			buttons.add(Button.primary("right-all", Emoji.fromMarkdown(ResponseUtils.RIGHT_ALL)));
		}

		List<ActionRow> rows = new ArrayList<>();
		List<Button> bs = new ArrayList<>();
		for (var b : buttons) {
			if (bs.size() == 5) {
				rows.add(ActionRow.of(bs));
				bs = new ArrayList<>();
			}
			bs.add(b);
		}
		if (!bs.isEmpty())
			rows.add(ActionRow.of(bs));

		msg.setActionRows(rows);
		var m = msg.complete();
		return new ActiveButtonBook(handler, pageHandler, maxPage, target, m, processor);
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public Consumer<ButtonClickEvent> getHandler() {
		return handler;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public BiConsumer<Integer, ButtonClickEvent> getPageHandler() {
		return pageHandler;
	}

	public InputProcessor<ButtonClickEvent> getProcessor() {
		return processor;
	}

	public User getTarget() {
		return target;
	}

	public boolean isEdgeButtons() {
		return edgeButtons;
	}

	public void remove(Button button) {
		buttons.remove(button);
	}

	public void setEdgeButtons(boolean edgeButtons) {
		this.edgeButtons = edgeButtons;
	}

	public void setHandler(Consumer<ButtonClickEvent> handler) {
		this.handler = handler;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public void setPageHandler(BiConsumer<Integer, ButtonClickEvent> pageHandler) {
		this.pageHandler = pageHandler;
	}

	public void setTarget(User target) {
		this.target = target;
	}

}
