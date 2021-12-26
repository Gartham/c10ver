package gartham.c10ver.response.utils;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.utils.Direction;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class DirectionSelector {
	private Button up = Button.primary("up", Emoji.fromMarkdown("\u2B06")),
			left = Button.primary("left", Emoji.fromMarkdown("\u2B05")),
			right = Button.primary("right", Emoji.fromMarkdown("\u27A1")),
			down = Button.primary("down", Emoji.fromMarkdown("\u2B07"));

	public static Direction getDirection(String id) {
		return switch (id) {
		case "up" -> Direction.UP;
		case "left" -> Direction.LEFT;
		case "right" -> Direction.RIGHT;
		case "down" -> Direction.DOWN;
		// TODO Consider using Enum's valueOf().
		default -> throw new IllegalArgumentException("Unexpected value: " + id);
		};
	}

	public static Direction getDirectionSelected(ButtonClickEvent event) {
		return getDirection(event.getComponentId());
	}

	public void disableUp() {
		up = up.asDisabled();
	}

	public void disableLeft() {
		left = left.asDisabled();
	}

	public void disableRight() {
		right = right.asDisabled();
	}

	public void disableDown() {
		down = down.asDisabled();
	}

	public void enableUp() {
		up = up.asEnabled();
	}

	public void enableLeft() {
		left = left.asEnabled();
	}

	public void enableRight() {
		right = right.asEnabled();
	}

	public void enableDown() {
		down = down.asEnabled();
	}

	public void enableAll() {
		enableDown();
		enableLeft();
		enableRight();
		enableUp();
	}

	public Button getButton(Direction direction) {
		return switch (direction) {
		case UP -> up;
		case LEFT -> left;
		case DOWN -> down;
		case RIGHT -> right;
		};
	}

	public void disable(Direction direction) {
		switch (direction) {
		case DOWN:
			disableDown();
			break;
		case LEFT:
			disableLeft();
			break;
		case RIGHT:
			disableRight();
			break;
		case UP:
			disableUp();
			break;
		}
	}

	public void disableAll() {
		disableDown();
		disableLeft();
		disableRight();
		disableUp();
	}

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