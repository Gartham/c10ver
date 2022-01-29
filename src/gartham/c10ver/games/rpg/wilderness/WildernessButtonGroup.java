package gartham.c10ver.games.rpg.wilderness;

import java.util.List;

import gartham.c10ver.response.ButtonGroup;
import gartham.c10ver.response.MutableButton;
import gartham.c10ver.response.utils.DirectionSelector;

class WildernessButtonGroup extends ButtonGroup {
	private final MutableButton d1 = create(), moveUp = create(DirectionSelector.UP_ENABLED), d2 = create(),
			moveLeft = create(DirectionSelector.LEFT_ENABLED), d3 = create(),
			moveRight = create(DirectionSelector.RIGHT_ENABLED), d4 = create(),
			moveDown = create(DirectionSelector.DOWN_ENABLED), rb1 = create(), rb2 = create(), rb3 = create(),
			rb4 = create(), rb5 = create(), rb6 = create(), bb1 = create(), bb2 = create(), bb3 = create(),
			bb4 = create(), bb5 = create(), pageLeft = create(), bb6 = create(), bb7 = create(), bb8 = create(),
			pageRight = create();

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

}
