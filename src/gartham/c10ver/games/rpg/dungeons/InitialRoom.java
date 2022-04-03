package gartham.c10ver.games.rpg.dungeons;

import gartham.c10ver.games.rpg.rooms.RectangularDungeonRoom;

public class InitialRoom extends DungeonRoom {

	public InitialRoom() {
		super(RectangularDungeonRoom.discordSquare(8));
	}

}
