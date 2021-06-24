package gartham.c10ver.games.rpg.rooms;

import java.util.Arrays;

public class SquareRoom implements Room {

	// Vertical (Single, Double), Horizontal
	private static final char[] SIDE = { '\u2502', '\u2551', '\u2500', '\u2550' },
			// Top (Right, Left), Bottom
			CORNERS = {};

	private final char[][] map;

	public SquareRoom(int size) {
		if (size < 3)
			throw new IllegalArgumentException("Square Rooms can't be smaller than 3 units!");
		this.map = new char[size][size];
		for (int i = 1; i < map.length - 1; i++) {
			map[i][0] = map[i][map[i].length - 1] = '\u2502';
			Arrays.fill(map[i], 1, map[i].length - 1, ' ');
		}
		Arrays.fill(map[0], 0, map[0].length - 1, '-');
		Arrays.fill(map[map.length - 1], 0, map[0].length - 1, '-');

	}

	@Override
	public char[][] layout() {
		var cl = new char[map.length][];
		for (int i = 0; i < map.length; i++)
			cl[i] = map[i].clone();
		return cl;
	}

}
