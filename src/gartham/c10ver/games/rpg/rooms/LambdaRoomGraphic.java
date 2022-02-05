package gartham.c10ver.games.rpg.rooms;

public interface LambdaRoomGraphic extends RoomGraphic {
	@Override
	default void render(String[][] map) {
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[i].length; j++) {
				var r = render(j, i);
				if (r != null)
					map[i][j] = r;
			}
	}

	String render(int x, int y);
}
