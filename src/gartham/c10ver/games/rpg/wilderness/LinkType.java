package gartham.c10ver.games.rpg.wilderness;

import java.util.List;

/**
 * Represents a type of link between two {@link WildernessTile}s. Instances are
 * used to get a {@link WildernessTile} that is in some way connected to another
 * {@link WildernessTile}. The most common type of connection is via physical
 * adjacency, which is captured by {@link AdjacencyLink}.
 * 
 * @author Gartham
 *
 */
public interface LinkType {
	enum AdjacencyLink implements RelativeLink {
		TOP(0, 1), RIGHT(1, 0), BOTTOM(0, -1), LEFT(-1, 0);

		private final int xs, ys;

		private AdjacencyLink(int xs, int ys) {
			this.xs = xs;
			this.ys = ys;
		}

		@Override
		public Location travelLink(Location from) {
			return Location.of(from.getX() + xs, from.getY() + ys);
		}

		public AdjacencyLink opposite() {
			return ITEMS.get((ordinal() + 2) % ITEMS.size());
		}

		private static final List<AdjacencyLink> ITEMS = List.of(values());

		public static List<AdjacencyLink> list() {
			return ITEMS;
		}

	}
}
