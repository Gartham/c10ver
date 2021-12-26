package gartham.c10ver.games.rpg.dungeons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gartham.c10ver.games.rpg.rooms.RectangularRoom;
import gartham.c10ver.utils.Direction;

public class Dungeon {
	private final List<DungeonRoom> rooms;
	private final int finalRoom;

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

	public static Dungeon simpleEasyDungeon() {
		int roomcount = (int) (Math.random() * 11 + 4);

		List<DungeonRoom> rooms = new ArrayList<>(), edges = new ArrayList<>();// Always contains a list of
																							// "edges".
		// These are extended as needed.

		var initialRoom = RectangularRoom.discordSquare((int) (Math.random() * 5 + 8));
		DungeonRoom firstdr = new DungeonRoom(initialRoom);
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
			var entry = e.getConnections().entrySet().iterator().next();
			List<Direction> potentialSides = new ArrayList<>(Direction.VALUES);
			potentialSides.remove(entry.getKey());// e (which is an edge) is connected to its parent via this side, so
													// for us to spawn a child off of e, we need to have the child
													// connect through a different side.
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
		RectangularRoom connection = RectangularRoom.discordSquare((int) (Math.random() * 5 + 8));
		DungeonRoom dr = new DungeonRoom(connection, new HashMap<>());
		dr.addConnection(side.opposite(), initial);
		initial.addConnection(side, dr);

		int pos = (int) (Math.random()
				* ((side.isHorizontal() ? initial.getRoom().getHeight() : initial.getRoom().getWidth()) - 4));
		initial.getRoom().createOpening(side, 4, pos);
		connection.createOpening(side.opposite(), 4,
				(int) (Math.random() * ((side.isHorizontal() ? connection.getHeight() : connection.getWidth()) - 4)));

		return dr;
	}

}
