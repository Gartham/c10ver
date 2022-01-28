package gartham.c10ver.games.rpg.wilderness;

import gartham.c10ver.games.rpg.rooms.RoomGraphic;
import static gartham.c10ver.games.rpg.rooms.RoomGraphic.*;

public class ExitGraphic implements RoomGraphic {

	@Override
	public void render(String[][] map) {
		map[centerHeight(map)][centerWidth(map)] = "\u26E9";
	}

}
