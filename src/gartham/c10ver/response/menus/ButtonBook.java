package gartham.c10ver.response.menus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.response.utils.ResponseUtils;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import static org.alixia.javalibrary.JavaTools.*;

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
		private final List<Button> buttons;
		private final boolean edgeButtons;

		private static List<ActionRow> genRows(Iterator<Button> buttons) {
			List<ActionRow> rows = new ArrayList<>();
			List<Button> bs = new ArrayList<>();
			while (buttons.hasNext()) {
				if (bs.size() == 5) {
					rows.add(ActionRow.of(bs));
					bs = new ArrayList<>();
				}
				bs.add(buttons.next());
			}
			if (!bs.isEmpty())
				rows.add(ActionRow.of(bs));
			return rows;
		}

		private Button disableLeft(Button button) {
			return page == 0 ? button.asDisabled() : button;
		}

		private Button disableRight(Button button) {
			return page == maxPage ? button.asDisabled() : button;
		}

		/**
		 * Disables all the buttons of this {@link ButtonBook} so that it can no longer
		 * be used.
		 */
		public void complete() {
			var itr = concat(
					iterator(Button.primary("left-one", Emoji.fromMarkdown(ResponseUtils.LEFT_ONE)).asDisabled()),
					mask(buttons.iterator(), Button::asDisabled),
					iterator(Button.primary("right-one", Emoji.fromMarkdown(ResponseUtils.RIGHT_ONE)).asDisabled()));
			if (edgeButtons)
				itr = concat(
						iterator(Button.primary("left-all", Emoji.fromMarkdown(ResponseUtils.LEFT_ALL)).asDisabled()),
						itr, iterator(
								Button.primary("right-all", Emoji.fromMarkdown(ResponseUtils.RIGHT_ALL)).asDisabled()));
			message.editMessageComponents(genRows(itr)).queue();
		}

		private List<ActionRow> genRows() {
			return genRows(buttons());
		}

		private Iterator<Button> buttons() {
			var itr = concat(
					iterator(disableLeft(Button.primary("left-one", Emoji.fromMarkdown(ResponseUtils.LEFT_ONE)))),
					buttons.iterator(),
					iterator(disableRight(Button.primary("right-one", Emoji.fromMarkdown(ResponseUtils.RIGHT_ONE)))));
			if (edgeButtons)
				itr = concat(
						iterator(disableLeft(Button.primary("left-all", Emoji.fromMarkdown(ResponseUtils.LEFT_ALL)))),
						itr, iterator(disableRight(
								Button.primary("right-all", Emoji.fromMarkdown(ResponseUtils.RIGHT_ALL)))));
			return itr;
		}

		private ActiveButtonBook(MessageAction msg, Consumer<ButtonClickEvent> handler,
				BiConsumer<Integer, ButtonClickEvent> pageHandler, int maxPage, User target, List<Button> buttons,
				InputProcessor<ButtonClickEvent> processor, boolean edgeButtons) {
			this.handler = handler;
			this.pageHandler = pageHandler;
			this.maxPage = maxPage;
			this.target = target;
			this.buttons = buttons;
			this.edgeButtons = edgeButtons;

			msg.setActionRows(genRows());
			var m = msg.complete();

			inc = (event, p, consumer) -> {

				if (event.getMessageIdLong() != m.getIdLong())
					return false;
				if (this.target != null && !this.target.equals(event.getUser())) {
					event.reply("That's not for you!").setEphemeral(true).queue();
					return true;
				}

				String e = event.getComponentId();

				switch (e) {
				case "left-all":
					if (this.page > 0) {
						page = 0;
						event.editComponents(genRows()).queue();
						this.pageHandler.accept(0, event);
					} else
						assert false : "A disabled \"left-all\" button was clicked?";
					return true;
				case "left-one":
					if (this.page > 0) {
						if (page == maxPage | --page == 0)
							event.editComponents(genRows()).queue();
						else
							event.deferEdit().queue();
						this.pageHandler.accept(this.page, event);
					} else
						assert false : "A disabled \"left-one\" button was clicked?";
					return true;
				case "right-one":
					if (this.page < this.maxPage) {
						if (++page == maxPage || page == 1)
							event.editComponents(genRows()).queue();
						else
							event.deferEdit().queue();
						this.pageHandler.accept(this.page, event);
					} else
						assert false : "A disabled \"right-one\" button was clicked?";
					return true;
				case "right-all":
					if (this.page < this.maxPage) {
						page = maxPage;
						event.editComponents(genRows()).queue();
						this.pageHandler.accept(this.page = this.maxPage, event);
					} else
						assert false : "A disabled \"right-all\" button was clicked?";
					return true;
				}

				this.handler.accept(event);
				if (oneTime)
					p.removeInputConsumer(consumer);
				return true;
			};
			(this.processor = processor).registerInputConsumer(inc);
			this.message = m;

		}

		/**
		 * Returns a copy of the {@link ActionRow}s of the {@link ActiveButtonBook} but
		 * such that the buttons in the {@link ActionRow}s are disabled. Otherwise, the
		 * {@link ActionRow} is a complete copy of the rows in the
		 * {@link ActiveButtonBook}. The {@link List} is fresh and mutable.
		 */
		public List<ActionRow> disabledButtonView() {
			List<ActionRow> ars = message.getActionRows(), nars = new ArrayList<>(ars.size());
			for (var ar : ars)
				nars.add(ActionRow.of(JavaTools.addAll(ar.getButtons(), Button::asDisabled,
						new ArrayList<>(ar.getButtons().size()))));
			return nars;
		}

		/**
		 * Returns a copy of the {@link ActionRow}s of the {@link ActiveButtonBook} but
		 * such that the buttons in the {@link ActionRow}s are enabled. Otherwise, the
		 * {@link ActionRow} is a complete copy of the rows in the
		 * {@link ActiveButtonBook}. The {@link List} is fresh and mutable.
		 */
		public List<ActionRow> enabledButtonView() {
			List<ActionRow> ars = message.getActionRows(), nars = new ArrayList<>(ars.size());
			for (var ar : ars)
				nars.add(ActionRow.of(
						JavaTools.addAll(ar.getButtons(), Button::asEnabled, new ArrayList<>(ar.getButtons().size()))));
			return nars;
		}

		public void disableButtons() {
			message.editMessageComponents(disabledButtonView()).queue();
		}

		public void enableButtons() {
			message.editMessageComponents(enabledButtonView()).queue();
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

	public Button add(String id, String emoji) {
		var b = Button.primary(id, Emoji.fromMarkdown(emoji));
		add(b);
		return b;
	}

	public Button add(String emoji) {
		return add(emoji, emoji);
	}

	public ActiveButtonBook attachAndSend(MessageAction msg) {
		return new ActiveButtonBook(msg, handler, pageHandler, maxPage, target, new ArrayList<>(this.buttons),
				processor, edgeButtons);
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
