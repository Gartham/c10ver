package gartham.c10ver.response.utils;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class DirectionSelector {
	private Button up = Button.primary("up", Emoji.fromMarkdown("\u2B06")),
			left = Button.primary("left", Emoji.fromMarkdown("\u2B05")),
			right = Button.primary("right", Emoji.fromMarkdown("\u27A1")),
			down = Button.primary("down", Emoji.fromMarkdown("\u2B07"));

	public Button getUp() {
		return up;
	}

	public Button getLeft() {
		return left;
	}

	public Button getRight() {
		return right;
	}

	public Button getDown() {
		return down;
	}

	public void setUp(Button up) {
		this.up = up;
	}

	public void setLeft(Button left) {
		this.left = left;
	}

	public void setRight(Button right) {
		this.right = right;
	}

	public void setDown(Button down) {
		this.down = down;
	}

	public List<ActionRow> actionRows() {
		List<ActionRow> row = new ArrayList<>();
		row.add(ActionRow.of(ResponseUtils.blockedButton("d1"), up, ResponseUtils.blockedButton("d2")));
		row.add(ActionRow.of(left, ResponseUtils.blockedButton("d3"), right));
		row.add(ActionRow.of(ResponseUtils.blockedButton("d4"), down, ResponseUtils.blockedButton("d5")));
		return row;
	}

	public static List<ActionRow> directionSelectorRows() {
		return directionSelector().actionRows();
	}

	public static DirectionSelector directionSelector() {
		return new DirectionSelector();
	}
}