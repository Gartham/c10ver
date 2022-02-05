package gartham.c10ver.games.rpg.rooms;

public interface XYLambdaRoomGraphic extends RoomGraphic {
	@Override
	default void render(String[][] map) {
		for (int y = map.length - 1; y >= 0; y--)
			for (int x = 0; x < map[y].length; x++) {
				var r = render(x, y);
				if (r != null)
					map[y][x] = r;
			}
	}

	String render(int x, int y);
}
