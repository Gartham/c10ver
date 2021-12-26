package gartham.c10ver.games.rpg.dungeons;

import java.util.Collections;
import java.util.Map;

import gartham.c10ver.games.rpg.rooms.RectangularRoom;

public class DungeonRoom<C> {
	private final RectangularRoom room;
	private final Map<C, DungeonRoom<C>> connections;

	public DungeonRoom(RectangularRoom room, Map<C, DungeonRoom<C>> connections) {
		this.room = room;
		this.connections = connections;
	}

	public RectangularRoom getRoom() {
		return room;
	}

	public Map<C, DungeonRoom<C>> getConnections() {
		return Collections.unmodifiableMap(connections);
	}

}
