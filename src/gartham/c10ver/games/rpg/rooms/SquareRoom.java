package gartham.c10ver.games.rpg.rooms;

import java.util.Arrays;

public class SquareRoom implements Room {

	// Vertical (Single, Double), Horizontal
	private static final char[] SIDE = { '\u2502', '\u2551', '\u2500', '\u2550' },
			// Top (Right, Left), Bottom
			CORNERS = { '\u2510', '\u250C', '\u2518', '\u2514' };

	private final char[][] map;

	public SquareRoom(int size) {
		if (size < 3)
			throw new IllegalArgumentException("Square Rooms can't be smaller than 3 units!");
		this.map = new char[size][size];
		for (int i = 1; i < map.length - 1; i++) {
			map[i][0] = map[i][map[i].length - 1] = SIDE[0];
			Arrays.fill(map[i], 1, map[i].length - 1, ' ');
		}
		Arrays.fill(map[0], 0, map[0].length - 1, SIDE[2]);
		Arrays.fill(map[map.length - 1], 0, map[0].length - 1, SIDE[2]);

		map[0][0] = CORNERS[1];
		map[0][size - 1] = CORNERS[0];
		map[size - 1][0] = CORNERS[3];
		map[size - 1][size - 1] = CORNERS[2];

	}

	@Override
	public char[][] layout() {
		var cl = new char[map.length][];
		for (int i = 0; i < map.length; i++)
			cl[i] = map[i].clone();
		return cl;
	}

}
