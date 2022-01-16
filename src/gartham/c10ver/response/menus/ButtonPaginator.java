package gartham.c10ver.response.menus;

import java.util.function.BiFunction;
import java.util.function.Function;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.response.utils.ResponseUtils;
import gartham.c10ver.utils.MessageActionHandler;
import gartham.c10ver.utils.MessageActionHandler.Action;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class ButtonPaginator {
	private final Action left, leftall, right, rightall;

	private static final Button LEFT_ONE = Button.primary("left-one", Emoji.fromMarkdown(ResponseUtils.LEFT_ONE)),
			RIGHT_ONE = Button.primary("right-one", Emoji.fromMarkdown(ResponseUtils.RIGHT_ONE)),
			LEFT_ALL = Button.primary("left-all", Emoji.fromMarkdown(ResponseUtils.LEFT_ALL)),
			RIGHT_ALL = Button.primary("right-all", Emoji.fromMarkdown(ResponseUtils.RIGHT_ALL));
	private final MessageActionHandler mah;

	public ButtonPaginator(MessageActionHandler mah, InputProcessor<ButtonClickEvent> processor) {
		left = mah.new Action(LEFT_ONE);
		right = mah.new Action(RIGHT_ONE);
		leftall = mah.new Action(LEFT_ALL);
		rightall = mah.new Action(RIGHT_ALL);
		this.processor = processor;
		this.mah = mah;
	}

	public ButtonPaginator(InputProcessor<ButtonClickEvent> processor) {
		this(new MessageActionHandler(), processor);
	}

	public MessageActionHandler.Action getLeft() {
		return left;
	}

	public MessageActionHandler.Action getLeftall() {
		return leftall;
	}

	public MessageActionHandler.Action getRight() {
		return right;
	}

	public MessageActionHandler.Action getRightall() {
		return rightall;
	}

	private boolean edgeButtons;
	private Function<ButtonClickEvent, Boolean> handler;
	private int maxPage = Integer.MAX_VALUE;
	private BiFunction<Integer, ButtonClickEvent, Boolean> pageHandler;// Returns true if handles message response.
	private final InputProcessor<ButtonClickEvent> processor;
	private User target;
	private InputConsumer<ButtonClickEvent> inc;
	private Message msg;
	private int page;
	private boolean oneTime;

	public boolean isEdgeButtons() {
		return edgeButtons;
	}

	public void setEdgeButtons(boolean edgeButtons) {
		this.edgeButtons = edgeButtons;
	}

	public boolean isOneTime() {
		return oneTime;
	}

	public void setOneTime(boolean oneTime) {
		this.oneTime = oneTime;
	}

	public void attachAndSend(MessageAction msg) {
		if (inc != null)
			throw new IllegalStateException("Already sent.");
		msg.setActionRows(mah.generate());
		this.msg = msg.complete();

		inc = (e, p, c) -> {
			if (e.getMessageIdLong() != this.msg.getIdLong())
				return false;
			else if (this.target != null && !this.target.equals(e.getUser())) {
				e.reply("That's not for you!").setEphemeral(true).queue();
				return true;
			}

			String id = e.getComponentId();
			switch (id) {
			case "left-all":
				if (page > 0) {
					page = 0;

					left.disable();
					leftall.disable();

					var r = pageHandler.apply(0, e);
					if (r == null || !r)
						e.editComponents(mah.generate()).queue();
				} else
					assert false : "A disabled \"left-all\" button was clicked in a ButtonPaginator.";
				return true;
			case "left-one":
				if (page > 0) {
					if (--page == 0) {
						left.disable();
						leftall.disable();
					}

					var r = pageHandler.apply(0, e);
					if (r == null || !r)
						if (page == 0)
							e.editComponents(mah.generate()).queue();
						else
							e.deferEdit().queue();

				} else
					assert false : "A disabled \"left-one\" button was clicked in a ButtonPaginator.";

				return true;
			case "right-one":
				if (page < maxPage) {
					if (++page == maxPage) {
						right.disable();
						rightall.disable();
					}

					var r = pageHandler.apply(page, e);
					if (r == null || !r)
						if (page == maxPage)
							e.editComponents(mah.generate()).queue();
						else
							e.deferEdit().queue();

				} else
					assert false : "A disabled \"right-one\" button was clicked in a ButtonPaginator.";

				return true;
			case "right-all":
				if (page < maxPage) {
					page = maxPage;

					right.disable();
					rightall.disable();

					var r = pageHandler.apply(maxPage, e);
					if (r == null || !r)
						e.editComponents(mah.generate()).queue();
				} else
					assert false : "A disabled \"right-all\" button was clicked in a ButtonPaginator.";
				return true;
			}

			var r = this.handler.apply(e);
			if (r == null || !r)
				mah.convert(Action::disable);
			if (oneTime)
				p.removeInputConsumer(c);

			return true;
		};
		processor.registerInputConsumer(inc);

	}

}
