package gartham.c10ver.games.rpg.dungeons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gartham.c10ver.games.rpg.rooms.RectangularRoom;
import gartham.c10ver.games.rpg.rooms.RectangularRoom.Opening;
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

	/**
	 * Adds the specified connection to this {@link DungeonRoom}. (This should be
	 * called on the provided room with the opposite direction in order to maintain
	 * system consistency.) Without calling
	 * <code>room.addConnection(dir.opposite(), this);</code> (with
	 * <code>room</code> being the {@link DungeonRoom} provided to this method,
	 * <code>dir</code> being the direction provided to this method, and
	 * <code>this</code> referring to this object), there will essentially be a
	 * "one-way" path from this {@link DungeonRoom} to the provided
	 * {@link DungeonRoom}.
	 * 
	 * @param room    The {@link DungeonRoom} connected to this {@link DungeonRoom}.
	 * @param opening The opening to add.
	 */
	public Path addPath(DungeonRoom room, Opening opening) {
		var p = new Path(room, opening);
		p.put();
		return p;
	}

	/**
	 * This method is the same as {@link #addPath(DungeonRoom, Opening)} except that
	 * it generates a randomly sized, randomly positioned {@link Opening} that fits
	 * in the wall specified by the provided {@link Direction}.
	 * 
	 * @param dir  The {@link Direction} for the wall to face.
	 * @param room The room connected to this room.
	 * @return The new {@link Path} created.
	 */
	public Path addRandomPath(Direction dir, DungeonRoom room) {
		var size = dir.isHorizontal() ? getRoom().getHeight() : getRoom().getWidth();
		var gw = (int) Math.random() * (size - 4) + 1;
		
		// Position should be Math.random() * (size - gw - 4 + 1).
		return addPath(room, getRoom().createOpening(dir, gw, (int) Math.random() * (size - gw - 3)));
	}

	public class Path {
		private final DungeonRoom to;
		private final Opening opening;

		public void remove() {
			connections.remove(opening.getDirection(), to);
		}

		public void put() {
			if (connections.containsKey(opening.getDirection()))
				throw new RuntimeException("Entry already exists.");
			connections.put(opening.getDirection(), to);
		}

		public DungeonRoom getTo() {
			return to;
		}

		public Opening getOpening() {
			return opening;
		}

		public Path(DungeonRoom to, Opening opening) {
			this.to = to;
			this.opening = opening;
		}

		public DungeonRoom getFrom() {
			return DungeonRoom.this;
		}
	}

	public static class Connection {
		private final Path first, second;

		public Connection(Path to, Path from) {
			this.second = to;
			this.first = from;
		}

		public Path getSecond() {
			return second;
		}

		public Path getFirst() {
			return first;
		}

		public void put() {
			if (second.getFrom().connections.containsKey(second.opening.getDirection())
					|| first.getFrom().connections.containsKey(first.opening.getDirection()))
				throw new IllegalStateException(
						"At least one of the DungeonRooms referred to by this Connection object is already connected.");
			first.getFrom().connections.put(first.opening.getDirection(), first.getTo());
			second.getFrom().connections.put(second.opening.getDirection(), second.getTo());
		}

		public void remove() {
			first.remove();
			second.remove();
		}
	}

	/**
	 * Connects this {@link DungeonRoom} to the specified {@link DungeonRoom}. This
	 * is similar to {@link #addPath(DungeonRoom, Opening)}, but this method
	 * maintains consistency of the dungeon by calling
	 * {@link #addPath(DungeonRoom, Opening)} on the other dungeon room with the
	 * arguments {@link Direction#opposite()} and <code>this</code>. This method
	 * will fail if either {@link DungeonRoom} has a connection in the requisite
	 * directions already.
	 * 
	 * @param dir   The {@link Direction} from the center of this
	 *              {@link DungeonRoom} of the wall that the other
	 *              {@link DungeonRoom} connects to.
	 * @param other The other {@link DungeonRoom}.
	 */
	public Connection connect(Direction dir, DungeonRoom other) {
		return new Connection(addRandomPath(dir, other), other.addRandomPath(dir.opposite(), this));
	}

	/**
	 * Gets a new {@link List} of {@link Direction}s in which there are connected
	 * dungeons.
	 * 
	 * @return A {@link List} of all the {@link Direction}s in which there are
	 *         connected dungeons.
	 */
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
