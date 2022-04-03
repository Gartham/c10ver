package gartham.c10ver.games.rpg.dungeons;

import gartham.c10ver.games.rpg.rooms.RectangularDungeonRoom;

public class EmptyRoom extends DungeonRoom {
	public EmptyRoom() {
		super(RectangularDungeonRoom.discordSquare((int) (Math.random() * 4 + 11)));
	}
}
