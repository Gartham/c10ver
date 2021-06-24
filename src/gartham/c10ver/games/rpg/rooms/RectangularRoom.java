package gartham.c10ver.games.rpg.rooms;

import java.util.Arrays;

public class RectangularRoom implements Room {

	// Vertical (Single, Double), Horizontal
	private static final char[] SIDE = { '\u2502', '\u2551', '\u2500', '\u2550' },
			// Top (Right, Left), Bottom
			CORNERS = { '\u2510', '\u250C', '\u2518', '\u2514' };

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

	@Override
	public char[][] layout() {
		var cl = new char[map.length][];
		for (int i = 0; i < map.length; i++)
			cl[i] = map[i].clone();
		return cl;
	}

}
