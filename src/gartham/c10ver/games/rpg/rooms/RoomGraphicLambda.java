package gartham.c10ver.games.rpg.rooms;

public interface RoomGraphicLambda extends RoomGraphic {
	@Override
	default void render(String[][] map) {
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map[i].length; j++)
				render(j, i);
	}

	String render(int x, int y);
}
