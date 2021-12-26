package gartham.c10ver.games.rpg.dungeons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gartham.c10ver.games.rpg.rooms.RectangularRoom;
import gartham.c10ver.response.utils.DirectionSelector;
import gartham.c10ver.utils.Direction;

public class DungeonRoom {
	private final RectangularRoom room;
	private final Map<Direction, DungeonRoom> connections;

	public DungeonRoom(RectangularRoom room, Map<Direction, DungeonRoom> connections) {
		this.room = room;
		this.connections = new HashMap<>(connections);
	}

	public DungeonRoom(RectangularRoom room) {
		this.room = room;
		this.connections = new HashMap<>();
	}

	public RectangularRoom getRoom() {
		return room;
	}

	public Map<Direction, DungeonRoom> getConnections() {
		return Collections.unmodifiableMap(connections);
	}

	/**
	 * Adds the specified connection to this {@link DungeonRoom}. (This should be
	 * called on the provided room with the opposite direction in order to maintain
	 * system consistency.)
	 * 
	 * @param dir  The direction of the wall that has the connection.
	 * @param room The {@link DungeonRoom} connected to this {@link DungeonRoom}.
	 */
	public void addConnection(Direction dir, DungeonRoom room) {
		if (connections.containsKey(dir))
			throw new RuntimeException("Entry already exists.");
		connections.put(dir, room);
	}

	public List<Direction> getConnectionDirections() {
		return new ArrayList<>(connections.keySet());
	}

	public Set<Direction> getConnectionDirectionsUnmodifiable() {
		return Collections.unmodifiableSet(connections.keySet());
	}

}
