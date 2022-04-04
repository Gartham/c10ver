package gartham.c10ver.response.buttonbox.pagination;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.response.buttonbox.ButtonBox;
import gartham.c10ver.response.utils.ResponseUtils;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public abstract class Paginator implements InputConsumer<ButtonClickEvent> {
	private final ButtonBox box;
	private Message message;
	private InputProcessor<ButtonClickEvent> buttonProcessor;
	private final ButtonBox.Button leftAll, left, rightAll, right;

	private int maxPage, page;

	public int getPage() {
		return page;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public ButtonBox.Button getLeftAll() {
		return leftAll;
	}

	public ButtonBox.Button getLeft() {
		return left;
	}

	public ButtonBox.Button getRightAll() {
		return rightAll;
	}

	public ButtonBox.Button getRight() {
		return right;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public Paginator(ButtonBox box) {
		this.box = box;

		leftAll = box.get(4, 0);
		left = box.get(4, 1);
		rightAll = box.get(4, 4);
		right = box.get(4, 3);

		leftAll.setID("left-all").setDisabled(true).setEmoji(Emoji.fromMarkdown(ResponseUtils.LEFT_ALL));
		left.setID("left").setDisabled(true).setEmoji(Emoji.fromMarkdown(ResponseUtils.LEFT_ONE));
		rightAll.setID("right-all").setDisabled(true).setEmoji(Emoji.fromMarkdown(ResponseUtils.RIGHT_ALL));
		right.setID("right").setDisabled(true).setEmoji(Emoji.fromMarkdown(ResponseUtils.RIGHT_ONE));
	}

	public void attach(Message message, InputProcessor<ButtonClickEvent> buttonProcessor) {
		(this.buttonProcessor = buttonProcessor).registerInputConsumer(this);
		this.message = message;
	}

	public void detach() {
		buttonProcessor.removeInputConsumer(this);
		message = null;
	}

	/**
	 * <p>
	 * Handles a {@link PaginationEvent} by optionally consuming it, and possibly
	 * altering this {@link Paginator}'s {@link Paginator#maxPage maxPage}. Any
	 * other processing tasks may be performed in this period.
	 * </p>
	 * <p>
	 * If the {@link PaginationEvent} is consumed, the {@link #page} is not updated,
	 * the {@link ButtonBox}'s buttons are not disabled or enabled (since it is
	 * assumed the page did not change), and the {@link ButtonClickEvent} is that
	 * sourced the provided {@link PaginationEvent} is not responded to
	 * automatically. This should be done by the handler.
	 * </p>
	 * <p>
	 * This method allows subclasses to handle scenarios where, for example, the
	 * wrong Discord user clicked a pagination button, in which case this method
	 * would consume the {@link PaginationEvent} and would respond to the user
	 * (ephemerally, for example).
	 * </p>
	 * <p>
	 * Note that {@link Paginator}s only
	 * {@link #consume(ButtonClickEvent, InputProcessor, InputConsumer)}
	 * {@link ButtonClickEvent}s for which the component ID is one of the four
	 * buttons managed by the {@link Paginator}. For any other buttons,
	 * {@link #consume(ButtonClickEvent, InputProcessor, InputConsumer)} returns
	 * <code>false</code>.
	 * </p>
	 * 
	 * @param event The event to handle.
	 */
	protected abstract void handle(PaginationEvent event);

	/**
	 * Processes the {@link ButtonClickEvent} responsible for a single pagination.
	 * The {@link #page} of this {@link Paginator} has been updated as per the
	 * button press and the {@link ButtonBox} backing this {@link Paginator} has
	 * been updated as well (so that the appropriate buttons were disabled or
	 * reenabled). This method call needs to respond to the {@link ButtonClickEvent}
	 * in some way, additionally updating the message's actionRows with the result
	 * of {@link #box}.
	 * 
	 * @param event The {@link ButtonClickEvent} to process.
	 */
	protected abstract void update(ButtonClickEvent event);

	private PaginationEvent fireEvent(ButtonClickEvent source) {
		int requestedPage;

		if (source.getComponentId().equals(left.getId()))
			requestedPage = page - 1;
		else if (source.getComponentId().equals(leftAll.getId()))
			requestedPage = 0;
		else if (source.getComponentId().equals(right.getId()))
			requestedPage = page + 1;
		else if (source.getComponentId().equals(rightAll.getId()))
			requestedPage = maxPage;
		else
			throw new RuntimeException("This should never happen.");

		PaginationEvent event = new PaginationEvent(this, page, requestedPage, source);
		handle(event);
		return event;
	}

	protected ButtonBox getBox() {
		return box;
	}

	@Override
	public boolean consume(ButtonClickEvent event, InputProcessor<? extends ButtonClickEvent> processor,
			InputConsumer<ButtonClickEvent> consumer) {
		if (!event.getMessageId().equals(message.getId()))
			return false;
		if (!(event.getComponentId().equals(left.getId()) || event.getComponentId().equals(right.getId())
				|| event.getComponentId().equals(leftAll.getId()) || event.getComponentId().equals(rightAll.getId()))) {
			return false;
		}

		var e = fireEvent(event);
		if (e.isConsumed())
			return true;
		// If the event is not consumed, set the current page to be whatever it should
		// be (and update buttons accordingly).
		// Note that the maxPage button may have been updated during the event's firing.

		if (e.getNewPage() < 0) {
			page = 0;
			left.disable();
			leftAll.disable();
		} else {
			left.enable();
			leftAll.enable();
		}

		if (maxPage < 0)
			maxPage = 0;
		if (e.getNewPage() > maxPage) {
			page = maxPage;
			right.disable();
			rightAll.disable();
		} else {
			right.enable();
			rightAll.enable();
		}

		return true;
	}

}