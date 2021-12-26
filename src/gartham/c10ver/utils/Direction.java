package gartham.c10ver.utils;

public enum Direction {
	UP, RIGHT, DOWN, LEFT;

	private static final Direction[] values = values();

	public boolean isHorizontal() {
		return this == UP || this == DOWN;
	}

	public boolean isVertical() {
		return !isHorizontal();
	}

	public Direction opposite() {
		return values[(ordinal() + 2) % values.length];
	}
}
