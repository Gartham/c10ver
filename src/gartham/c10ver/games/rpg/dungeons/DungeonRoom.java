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

	private boolean claimed;

	public boolean isClaimed() {
		return claimed;
	}

	public void setClaimed(boolean claimed) {
		this.claimed = claimed;
	}

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

	/**
	 * Connects this {@link DungeonRoom} to the specified {@link DungeonRoom}. This
	 * is similar to {@link #addConnection(Direction, DungeonRoom)}, but this method
	 * maintains consistency of the dungeon by calling
	 * {@link #addConnection(Direction, DungeonRoom)} on the other dungeon room with
	 * the arguments {@link Direction#opposite()} and <code>this</code>. This method
	 * will fail if either {@link DungeonRoom} has a connection in the requisite
	 * directions already.
	 * 
	 * @param dir   The {@link Direction} from the center of this
	 *              {@link DungeonRoom} of the wall that the other
	 *              {@link DungeonRoom} connects to.
	 * @param other The other {@link DungeonRoom}.
	 */
	public void connect(Direction dir, DungeonRoom other) {
		if (connections.containsKey(dir) || other.connections.containsKey(dir.opposite()))
			throw new IllegalStateException("At least one of the DungeonRooms is already connected.");
		connections.put(dir, other);
		other.connections.put(dir.opposite(), this);
	}

	public List<Direction> getConnectionDirections() {
		return new ArrayList<>(connections.keySet());
	}

	public Set<Direction> getConnectionDirectionsUnmodifiable() {
		return Collections.unmodifiableSet(connections.keySet());
	}

	public void prepare(DirectionSelector selector, String actionButtonID) {
		selector.reset();
		selector.disableDirections();
		for (var v : connections.keySet())
			selector.enable(v);
	}

	/**
	 * Gets the {@link DungeonRoom} connected to this {@link DungeonRoom} in the
	 * {@link Direction} specified.
	 * 
	 * @param direction The direction.
	 * @return The {@link DungeonRoom}, or <code>null</code> if no
	 *         {@link DungeonRoom} exists in that {@link Direction}.
	 */
	public DungeonRoom getRoom(Direction direction) {
		return connections.get(direction);
	}

}
