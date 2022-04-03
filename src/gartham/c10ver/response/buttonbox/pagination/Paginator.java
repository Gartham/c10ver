package gartham.c10ver.response.buttonbox.pagination;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.response.buttonbox.ButtonBox;
import gartham.c10ver.response.utils.ResponseUtils;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public class Paginator implements InputConsumer<ButtonClickEvent> {
	private final ButtonBox box;
	private Message message;
	private InputProcessor<ButtonClickEvent> buttonProcessor;
	private final ButtonBox.Button leftAll, left, rightAll, right;

	private int maxPage;

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

	@Override
	public boolean consume(ButtonClickEvent event, InputProcessor<? extends ButtonClickEvent> processor,
			InputConsumer<ButtonClickEvent> consumer) {
		if (!event.getMessageId().equals(message.getId()))
			return false;

		return false;
	}

}
