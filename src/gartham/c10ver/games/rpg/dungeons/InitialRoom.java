package gartham.c10ver.games.rpg.dungeons;

import gartham.c10ver.games.rpg.rooms.RectangularRoom;

public class InitialRoom extends DungeonRoom {

	public InitialRoom() {
		super(RectangularRoom.discordSquare(8));
	}

}
