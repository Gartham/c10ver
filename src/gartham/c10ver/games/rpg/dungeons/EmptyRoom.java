package gartham.c10ver.games.rpg.dungeons;

import gartham.c10ver.games.rpg.rooms.RectangularRoom;

public class EmptyRoom extends DungeonRoom {
	public EmptyRoom() {
		super(RectangularRoom.discordSquare((int) (Math.random() * 4 + 11)));
	}
}
