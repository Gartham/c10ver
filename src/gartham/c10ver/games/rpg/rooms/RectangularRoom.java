package gartham.c10ver.games.rpg.rooms;

import java.util.Arrays;

import gartham.c10ver.utils.Direction;

public class RectangularRoom implements StringRoom {

	// Vertical (Single, Double), Horizontal
	private static final char[] SIDE = { '\u2502', '\u2551', '\u2500', '\u2550' },
			// Top (Right, Left), Bottom
			CORNERS = { '\u2510', '\u250C', '\u2518', '\u2514' }, SPLITTERS = { '\u2534', // Horizontal Up
					'\u252C', // Horizontal Down
					'\u251C', // Vertical Right
					'\u2524' // Vertical Left
			};

	// vertical, horizontal - [3][0] is the fourth row and the first column.
	private final char[][] map;
	private final int width, height;

	public RectangularRoom(int width, int height) {
		if (width < 3 || height < 3)
			throw new IllegalArgumentException("Room can't be smaller than 3 units!");
		this.map = new char[this.height = height][this.width = width];
		for (int i = 1; i < map.length - 1; i++) {
			map[i][0] = map[i][map[i].length - 1] = SIDE[0];
			Arrays.fill(map[i], 1, map[i].length - 1, ' ');
		}
		Arrays.fill(map[0], 0, map[0].length - 1, SIDE[2]);
		Arrays.fill(map[map.length - 1], 0, map[0].length - 1, SIDE[2]);

		map[0][0] = CORNERS[1];
		map[0][width - 1] = CORNERS[0];
		map[height - 1][0] = CORNERS[3];
		map[height - 1][width - 1] = CORNERS[2];

	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	/**
	 * Creates an opening in this room on the wall of this room corresponding to the
	 * specified {@link Direction}. The width of the actual opening is the gap
	 * width; the gap width does not include the 2 characters around the opening
	 * that are used to convey that there is an opening. The position is how far
	 * down (for vertical sides, {@link Direction#RIGHT} or {@link Direction#LEFT})
	 * or how far to the right (for horizontal sides, {@link Direction#UP} or
	 * {@link Direction#DOWN}) the opening should be. The boundaries of the wall
	 * selected are taken into account when specifying the gap position and the
	 * position is clamped so that the opening will fit on the wall. If a gap is too
	 * large to fit in a wall, it is shrunk to fit. Clamping is prioritized such
	 * that if a wall of the specified gap-width cannot fit at the specified
	 * position, but can fit on the wall, it is moved so that it fits. (Position is
	 * mutated preferentially to gap-width.) Note that this method does not take
	 * previous calls (so previous gaps) into account, so previous gaps may be
	 * overwritten entirely or partially, leading to strange rendering anomalies if
	 * care is not taken by callers.
	 * 
	 * @param side     The {@link Direction} of the room to put an opening in the
	 *                 wall of.
	 * @param gapwidth The width of the opening. (The number of empty characters
	 *                 wide the opening should be.)
	 * @param pos      The shift from the top or right (depending on whether the
	 *                 wall runs vertically or horizontally, respectively) that the
	 *                 opening should be made.
	 */
	public void createOpening(Direction side, int gapwidth, int pos) {
		if (side.isHorizontal())
			createHorizontalOpening(side == Direction.UP ? map[0] : map[map.length - 1], gapwidth, pos);
		else {
			int shift = side == Direction.RIGHT ? width - 1 : 0;
			if ((gapwidth += 2) > height - 2)
				gapwidth = height - 2;
			else if (gapwidth < 3)
				gapwidth = 3;

			if (pos + gapwidth > height - 2)
				pos -= pos + gapwidth - height + 2;
			else if (pos < 0)
				pos = 0;

			map[pos + 1][shift] = SPLITTERS[0];
			for (int i = 2; i < gapwidth; i++)
				map[pos + i][shift] = ' ';
			map[pos + gapwidth][shift] = SPLITTERS[1];
		}
	}

	private void createHorizontalOpening(char[] arr, int width, int shift) {
		if ((width += 2) > arr.length - 2)
			width = arr.length - 2;
		else if (width < 3)
			width = 3;

		// Width is now the width of the gap + the two splitter characters.

		if (shift + width > arr.length - 2)
			shift -= shift + width - arr.length + 2;
		else if (shift < 0)
			shift = 0;

		arr[shift + 1] = SPLITTERS[3];
		Arrays.fill(arr, shift + 2, shift + width, ' ');
		arr[shift + width] = SPLITTERS[2];
	}

	@Override
	public char[][] tilemap() {
		var cl = new char[map.length][];
		for (int i = 0; i < map.length; i++)
			cl[i] = map[i].clone();
		return cl;
	}

	/**
	 * Returns a {@link RectangularRoom} which renders as a square on Discord in a
	 * code block. (The width is a factor of <code>2.2</code> of the height, since
	 * the width of a character on Discord is less than the height in code blocks.)
	 * <code>width*2.2 = height</code>
	 * 
	 * @param size The height of the square.
	 * @return The new {@link RectangularRoom}.
	 */
	public static RectangularRoom discordSquare(int size) {
		return new RectangularRoom((int) (2.2 * size), size);
	}

}
