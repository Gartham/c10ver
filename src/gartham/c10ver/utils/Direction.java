package gartham.c10ver.utils;

import java.util.List;

public enum Direction {
	UP, RIGHT, DOWN, LEFT;

	private static final Direction[] values = values();

	/**
	 * Returns <code>true</code> if the wall of a square this {@link Direction} of
	 * the center of a square runs horizontally.
	 * 
	 * @return <code>this == UP || this == DOWN</code>
	 */
	public boolean isHorizontal() {
		return this == UP || this == DOWN;
	}

	/**
	 * Returns <code>true</code> if the wall of a square this {@link Direction} of
	 * the center of a square runs vertically.
	 * 
	 * @return <code>this == LEFT || this == RIGHT</code>
	 */
	public boolean isVertical() {
		return !isHorizontal();
	}

	public Direction opposite() {
		return values[(ordinal() + 2) % values.length];
	}

	public static final List<Direction> VALUES = List.of(values);

}
