package gartham.c10ver.games.rpg.wilderness;

import java.util.List;

import gartham.c10ver.response.ButtonGroup;
import gartham.c10ver.response.MutableButton;
import gartham.c10ver.response.menus.ButtonBook;
import gartham.c10ver.response.utils.DirectionSelector;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.interactions.components.Button;

class WildernessGamepad extends ButtonGroup {

	public static final Button D1 = blank("d1"), MOVE_UP = (DirectionSelector.UP_ENABLED), D2 = blank("d2"),
			MOVE_LEFT = (DirectionSelector.LEFT_ENABLED), D3 = blank("d3"),
			MOVE_RIGHT = (DirectionSelector.RIGHT_ENABLED), D4 = blank("d4"),
			MOVE_DOWN = (DirectionSelector.DOWN_ENABLED), D5 = blank("d5"), RB1 = redBlank("rb1"),
			RB2 = redBlank("rb2"), RB3 = redBlank("rb3"), RB4 = redBlank("rb4"), RB5 = redBlank("rb5"),
			RB6 = redBlank("rb6"), BB1 = greenBlank("bb1"), BB2 = greenBlank("bb2"), BB3 = greenBlank("bb3"),
			BB4 = greenBlank("bb4"), BB5 = greenBlank("bb5"), PAGE_LEFT = ButtonBook.LEFT_ONE, BB6 = greenBlank("bb6"),
			BB7 = greenBlank("bb7"), BB8 = greenBlank("bb8"), PAGE_RIGHT = ButtonBook.RIGHT_ONE;

	public final static Button blank(String id) {
		return Button.secondary(id, Utilities.ZERO_WIDTH_SPACE).asDisabled();
	}

	public static final Button redBlank(String id) {
		return Button.danger(id, Utilities.ZERO_WIDTH_SPACE).asDisabled();
	}

	public static final Button greenBlank(String id) {
		return Button.success(id, Utilities.ZERO_WIDTH_SPACE).asDisabled();
	}

	private final MutableButton d1 = create(D1), moveUp = create(DirectionSelector.UP_ENABLED), d2 = create(D2),
			rb1 = create(RB1), rb2 = create(RB2), moveLeft = create(DirectionSelector.LEFT_ENABLED), d3 = create(D3),
			moveRight = create(DirectionSelector.RIGHT_ENABLED), rb3 = create(RB3), rb4 = create(RB4), d4 = create(D4),
			moveDown = create(DirectionSelector.DOWN_ENABLED), d5 = create(D5), rb5 = create(RB5), rb6 = create(RB6),
			bb1 = create(BB1), bb2 = create(BB2), bb3 = create(BB3), bb4 = create(BB4), bb5 = create(BB5),
			pageLeft = create(PAGE_LEFT), bb6 = create(BB6), bb7 = create(BB7), bb8 = create(BB8),
			pageRight = create(PAGE_RIGHT);

	public static final long RIGHT_BUTTON_COUNT = 6, BOTTOM_BUTTON_COUNT = 8;

	public List<MutableButton> rbs() {
		return List.of(rb1, rb2, rb3, rb4, rb5, rb6);
	}

	public List<MutableButton> bbs() {
		return List.of(bb1, bb2, bb3, bb4, bb5, bb6, bb7, bb8);
	}

	public List<MutableButton> optionButtons() {
		return List.of(rb1, rb2, rb3, rb4, rb5, rb6, bb1, bb2, bb3, bb4, bb5, bb6, bb7, bb8);
	}

	public MutableButton getD1() {
		return d1;
	}

	public MutableButton getMoveUp() {
		return moveUp;
	}

	public MutableButton getD2() {
		return d2;
	}

	public MutableButton getMoveLeft() {
		return moveLeft;
	}

	public MutableButton getD3() {
		return d3;
	}

	public MutableButton getMoveRight() {
		return moveRight;
	}

	public MutableButton getD4() {
		return d4;
	}

	public MutableButton getMoveDown() {
		return moveDown;
	}

	public MutableButton getRb1() {
		return rb1;
	}

	public MutableButton getRb2() {
		return rb2;
	}

	public MutableButton getRb3() {
		return rb3;
	}

	public MutableButton getRb4() {
		return rb4;
	}

	public MutableButton getRb5() {
		return rb5;
	}

	public MutableButton getRb6() {
		return rb6;
	}

	public MutableButton getBb1() {
		return bb1;
	}

	public MutableButton getBb2() {
		return bb2;
	}

	public MutableButton getBb3() {
		return bb3;
	}

	public MutableButton getBb4() {
		return bb4;
	}

	public MutableButton getBb5() {
		return bb5;
	}

	public MutableButton getPageLeft() {
		return pageLeft;
	}

	public MutableButton getBb6() {
		return bb6;
	}

	public MutableButton getBb7() {
		return bb7;
	}

	public MutableButton getBb8() {
		return bb8;
	}

	public MutableButton getPageRight() {
		return pageRight;
	}

	public void applyDirectionSelector() {
		d1.setButton(D1);
		moveUp.setButton(MOVE_UP);
		d2.setButton(D2);
		moveLeft.setButton(MOVE_LEFT);
		d3.setButton(D3);
		moveRight.setButton(MOVE_RIGHT);
		d4.setButton(D4);
		moveDown.setButton(MOVE_DOWN);
		d5.setButton(D5);
	}

	public void clearRightButtons() {
		rb1.setButton(RB1);
		rb2.setButton(RB2);
		rb3.setButton(RB3);
		rb4.setButton(RB4);
		rb5.setButton(RB5);
		rb6.setButton(RB6);
	}

	public void clearBottomButtons() {
		bb1.setButton(BB1);
		bb2.setButton(BB2);
		bb3.setButton(BB3);
		bb4.setButton(BB4);
		bb5.setButton(BB5);
		bb6.setButton(BB6);
		bb7.setButton(BB7);
		bb8.setButton(BB8);
	}

	public void applyPageNavigationButtons() {
		pageLeft.setButton(PAGE_LEFT);
		pageRight.setButton(PAGE_RIGHT);
	}

	public MutableButton getD5() {
		return d5;
	}

}
