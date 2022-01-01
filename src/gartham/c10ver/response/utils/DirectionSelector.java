package gartham.c10ver.response.utils;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.utils.Direction;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class DirectionSelector {

	public static final Button UP_ENABLED = Button.primary("up", Emoji.fromMarkdown("\u2B06")),
			LEFT_ENABLED = Button.primary("left", Emoji.fromMarkdown("\u2B05")),
			RIGHT_ENABLED = Button.primary("right", Emoji.fromMarkdown("\u27A1")),
			DOWN_ENABLED = Button.primary("down", Emoji.fromMarkdown("\u2B07")),
			UP_DISABLED = Button.secondary("up", Emoji.fromMarkdown("\u2B06")).asDisabled(),
			LEFT_DISABLED = Button.secondary("left", Emoji.fromMarkdown("\u2B05")).asDisabled(),
			RIGHT_DISABLED = Button.secondary("right", Emoji.fromMarkdown("\u27A1")).asDisabled(),
			DOWN_DISABLED = Button.secondary("down", Emoji.fromMarkdown("\u2B07")).asDisabled(),
			DEFAULT_TOP_LEFT = ResponseUtils.blockedButton("d1"), DEFAULT_TOP_RIGHT = ResponseUtils.blockedButton("d2"),
			DEFAULT_MIDDLE = ResponseUtils.blockedButton("d3"), DEFAULT_BOTTOM_LEFT = ResponseUtils.blockedButton("d4"),
			DEFAULT_BOTTOM_RIGHT = ResponseUtils.blockedButton("d5");
	private Button up = UP_ENABLED, left = LEFT_ENABLED, right = RIGHT_ENABLED, down = DOWN_ENABLED,
			topLeft = DEFAULT_TOP_LEFT, topRight = DEFAULT_TOP_RIGHT, middle = DEFAULT_MIDDLE,
			bottomLeft = DEFAULT_BOTTOM_LEFT, bottomRight = DEFAULT_BOTTOM_RIGHT;

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
		up = UP_DISABLED;
	}

	public void disableLeft() {
		left = LEFT_DISABLED;
	}

	public Button getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(Button topLeft) {
		this.topLeft = topLeft;
	}

	public Button getTopRight() {
		return topRight;
	}

	public void setTopRight(Button topRight) {
		this.topRight = topRight;
	}

	public Button getMiddle() {
		return middle;
	}

	public void setMiddle(Button middle) {
		this.middle = middle;
	}

	public Button getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(Button bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	public Button getBottomRight() {
		return bottomRight;
	}

	public void setBottomRight(Button bottomRight) {
		this.bottomRight = bottomRight;
	}

	public void disableRight() {
		right = RIGHT_DISABLED;
	}

	public void disableDown() {
		down = DOWN_DISABLED;
	}

	public void enableUp() {
		up = UP_ENABLED;
	}

	public void enableLeft() {
		left = LEFT_ENABLED;
	}

	public void enableRight() {
		right = RIGHT_ENABLED;
	}

	public void enableDown() {
		down = DOWN_ENABLED;
	}

	public void enableDirections() {
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

	public void disableDirections() {
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

	public void resetTopLeft() {
		topLeft = DEFAULT_TOP_LEFT;
	}

	public void resetTopRight() {
		topRight = DEFAULT_TOP_RIGHT;
	}

	public void resetMiddle() {
		middle = DEFAULT_MIDDLE;
	}

	public void resetBottomLeft() {
		bottomLeft = DEFAULT_BOTTOM_LEFT;
	}

	public void resetBottomRight() {
		bottomRight = DEFAULT_BOTTOM_RIGHT;
	}

	public List<ActionRow> actionRows() {
		List<ActionRow> row = new ArrayList<>();
		row.add(ActionRow.of(topLeft, up, topRight));
		row.add(ActionRow.of(left, middle, right));
		row.add(ActionRow.of(bottomLeft, down, bottomRight));
		return row;
	}

	public static List<ActionRow> directionSelectorRows() {
		return directionSelector().actionRows();
	}

	public static DirectionSelector directionSelector() {
		return new DirectionSelector();
	}

	public void enable(Direction v) {
		switch (v) {
		case DOWN:
			enableDown();
			break;
		case LEFT:
			enableLeft();
			break;
		case RIGHT:
			enableRight();
			break;
		case UP:
			enableUp();
			break;
		}
	}

	public void reset() {
		up = UP_ENABLED;
		left = LEFT_ENABLED;
		right = RIGHT_ENABLED;
		down = DOWN_ENABLED;
		topLeft = DEFAULT_TOP_LEFT;
		topRight = DEFAULT_TOP_RIGHT;
		middle = DEFAULT_MIDDLE;
		bottomLeft = DEFAULT_BOTTOM_LEFT;
		bottomRight = DEFAULT_BOTTOM_RIGHT;
	}
}