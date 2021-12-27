package gartham.c10ver.games.rpg.dungeons;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonTeam;
import gartham.c10ver.games.rpg.rooms.RectangularRoom;
import gartham.c10ver.response.utils.DirectionSelector;
import gartham.c10ver.utils.Direction;
import net.dv8tion.jda.api.interactions.components.Button;

public class DungeonRoom {
	private final RectangularRoom room;
	private final Map<Direction, DungeonRoom> connections;
	private final RoomTraits traits;
	private boolean claimed;

	public boolean isClaimed() {
		return claimed;
	}

	public void setClaimed(boolean claimed) {
		this.claimed = claimed;
	}

	public static class RoomTraits {
		public enum Type {
			EMPTY, CLOVES, LOOT, FIGHT;
		}

		private final Type type; // Changes the color of the Discord embed.
		private final BigInteger cloves;
		private final RewardsOperation loot;
		private final GarmonTeam enemies;

		public RoomTraits(BigInteger cloves) {
			type = Type.CLOVES;
			this.cloves = cloves;
			loot = null;
			enemies = null;
		}

		public RoomTraits(RewardsOperation loot) {
			type = Type.LOOT;
			this.loot = loot;
			cloves = null;
			enemies = null;
		}

		public RoomTraits(GarmonTeam enemies) {
			type = Type.FIGHT;
			this.enemies = enemies;
			cloves = null;
			loot = null;
		}

		public RoomTraits() {
			type = Type.EMPTY;
			cloves = null;
			loot = null;
			enemies = null;
		}

		public BigInteger cloves() {
			return cloves;
		}

		public RewardsOperation loot() {
			return loot;
		}

		public GarmonTeam enemies() {
			return enemies;
		}

		public Type getType() {
			return type;
		}
	}

	public DungeonRoom(RectangularRoom room, Map<Direction, DungeonRoom> connections, RoomTraits traits) {
		this.room = room;
		this.connections = new HashMap<>(connections);
		this.traits = traits;
	}

	public DungeonRoom(RectangularRoom room, RoomTraits traits) {
		this.room = room;
		this.connections = new HashMap<>();
		this.traits = traits;
	}

	public RoomTraits getTraits() {
		return traits;
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

	@SuppressWarnings("incomplete-switch")
	public void prepare(DirectionSelector selector, Button actionButton) {
		selector.reset();
		selector.disableDirections();
		for (var v : connections.keySet())
			selector.enable(v);
		if (!isClaimed())
			switch (traits.type) {
			case CLOVES:
			case LOOT:
				selector.setMiddle(actionButton);
			}
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
