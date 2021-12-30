package gartham.c10ver.games.rpg.dungeons;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gartham.c10ver.economy.AbstractMultiplier;
import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.games.rpg.creatures.Creature;
import gartham.c10ver.games.rpg.creatures.Nymph;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonFighter;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonTeam;
import gartham.c10ver.utils.Direction;

public class Dungeon {
	private final List<DungeonRoom> rooms;
	private final int finalRoom;

	public List<DungeonRoom> getRooms() {
		return Collections.unmodifiableList(rooms);
	}

	public int index(DungeonRoom dr) {
		return rooms.indexOf(dr);
	}

	private Dungeon(List<DungeonRoom> rooms, int finalRoom) {
		this.rooms = rooms;
		this.finalRoom = finalRoom;
	}

	public DungeonRoom getInitialRoom() {
		return rooms.get(0);
	}

	public DungeonRoom getFinalRoom() {
		return rooms.get(finalRoom);
	}

	public static DungeonRoom generateRandomRoom() {
		var rand = Math.random();
		if (rand < .7) {
			return new EmptyRoom();
		} else if (rand < 0.8) {
			List<Creature> creechurrs = new ArrayList<>();
			var enemy = new Nymph();
			if (Math.random() < 0.3)
				creechurrs.add(new Nymph());
			GarmonTeam team = new GarmonTeam("Wilderness", new GarmonFighter(enemy));
			return new EnemyRoom(team);
		} else if (rand < 0.95)
			return new LootRoom(
					new RewardsOperation().setCloves(BigInteger.valueOf((long) (Math.random() * 158 + 32))));
		else {
			var ro = new RewardsOperation();
			ro.getMults().put(generateRandomMultiplier(), 1);
			if (Math.random() < .2) {
				AbstractMultiplier val = generateRandomMultiplier();
				if (ro.getMults().containsKey(val))
					ro.getMults().put(val, ro.getMults().get(val) + 1);
			}
			return new LootRoom(ro);
		}
	}

	private static AbstractMultiplier generateRandomMultiplier() {
		return AbstractMultiplier.ofMin(Math.random() < .5 ? 1 : Math.random() < .5 ? 5 : 10, // TODO
				BigDecimal.valueOf(Math.random() < .5 ? .5 : Math.random() < .1 ? .15 : .25));
	}

	public static Dungeon simpleEasyDungeon() {
		int roomcount = (int) (Math.random() * 11 + 4);

		List<DungeonRoom> rooms = new ArrayList<>(), edges = new ArrayList<>();// Always contains a list of
																				// "edges".
		// These are extended as needed.

		DungeonRoom firstdr = new InitialRoom();
		rooms.add(firstdr);
		roomcount--;// First room.

		Direction firstSide = Direction.VALUES.get((int) (Direction.VALUES.size() * Math.random()));
		var next = build(firstdr, firstSide);
		rooms.add(next);
		edges.add(next);
		roomcount--;// Second room.

		if (Math.random() < 0.5) {
			var sides = new ArrayList<>(Direction.VALUES);
			sides.remove(firstSide);
			Direction side = sides.get((int) (Math.random() * sides.size()));
			next = build(firstdr, side);
			rooms.add(next);
			edges.add(next);
			roomcount--;// Third room.
		}

		while (roomcount > 0) {
			int index = (int) (Math.random() * edges.size());
			var e = edges.get(index);
			var entry = e.getConnectionDirectionsUnmodifiable().iterator().next();
			List<Direction> potentialSides = new ArrayList<>(Direction.VALUES);
			potentialSides.remove(entry);// e (which is an edge) is connected to its parent via this side, so for us to
											// spawn a child off of e, we need to have the child connect through a
											// different side.
			var s = potentialSides.get((int) (Math.random() * potentialSides.size()));
			var child = build(e, s);
			rooms.add(child);
			edges.add(child);
			edges.remove(index);// Remove the edge we just spawned a child for; it is no longer an edge because
								// it has a child connected to it.
			roomcount--;

			// Small chance of having a "third" connection.
			if (Math.random() < 0.25 && roomcount > 0) {
				potentialSides.remove(s);// Remove the side we just spawned a child from from the list of potential
											// sides.
				s = potentialSides.get((int) (Math.random() * potentialSides.size()));// Pick a new random side to spawn
																						// a new child from.

				child = build(e, s);// Build this second child.
				rooms.add(child);// Add the child.
				edges.add(child);//

				// We do not need to remove e from edges (because we already did).
				roomcount--;
			}
		}

		return new Dungeon(rooms, (int) (Math.random() * edges.size()));
	}

	private static DungeonRoom build(DungeonRoom initial, Direction side) {
		DungeonRoom dr = generateRandomRoom();
		initial.connect(side, dr);
		return dr;
	}

}
