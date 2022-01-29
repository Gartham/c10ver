package gartham.c10ver.games.rpg.rooms;

public interface RoomGraphic {
	/**
	 * Renders this {@link RoomGraphic} onto the provided map.
	 * 
	 * @param map The map to render onto.
	 */
	void render(String[][] map);

	static void render(String[][] map, int d, int b, boolean solid, String[]... icon) {
		// Nulls are treated as no-op.
		if (solid)
			for (int i = d; i < icon.length + d; i++)
				System.arraycopy(icon[i - d], 0, map[i], b, icon[i].length);
		else
			for (int i = d; i < icon.length + d; i++)
				for (int j = b; j < icon[i].length + b; j++)
					map[i][j] = icon[i - d][j - b];
	}

	static void renderAtMapCenter(String[][] map, boolean solid, String[]... icon) {
		render(map, centerHeight(map), centerWidth(map), solid, icon);
	}

	static void renderSolid(String[][] map, int d, int b, String[]... icon) {
		render(map, d, b, true, icon);
	}

	static void renderOverlay(String[][] map, int d, int b, String[]... icon) {
		render(map, d, b, false, icon);
	}

	static int centerHeight(String[][] map) {
		return map.length / 2;
	}

	static int centerWidth(String[][] map) {
		return map[0].length / 2;
	}

	static int getCenteringDepth(String[][] map, String[][] icon) {
		return map.length / 2 - icon.length / 2;
	}

	static int getCenteringBreadth(String[][] map, String[][] icon) {
		return map[0].length / 2 - icon[0].length / 2;
	}
}