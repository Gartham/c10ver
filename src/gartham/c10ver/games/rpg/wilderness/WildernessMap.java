package gartham.c10ver.games.rpg.wilderness;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gartham.c10ver.games.rpg.wilderness.LinkType.AdjacencyLink;

public abstract class WildernessMap<W extends gartham.c10ver.games.rpg.wilderness.WildernessMap<W>.WildernessTile> {

	private final Map<Location, W> tilemap = new HashMap<>();
	private W origin;

	protected WildernessMap() {
	}

	public W getOrigin() {
		return origin;
	}

	public W get(int x, int y) {
		return tilemap.get(new Location(x, y));
	}

	public abstract class WildernessTile {
		private final Map<LinkType, W> linkedTiles = new HashMap<>(2);
		private final Location location;

		public Location getLocation() {
			return location;
		}

		public Map<LinkType, W> getLinkedTiles() {
			return Collections.unmodifiableMap(linkedTiles);
		}

		public W get(LinkType link) {
			return linkedTiles.get(link);
		}

		@SuppressWarnings("unchecked")
		protected WildernessTile(int x, int y) {

			if (origin == null)
				if (x == 0 && y == 0)
					origin = (W) this;
				else
					throw new IllegalStateException("Map already initialized with initial tile.");
			else if (origin == null && (x != 0 || y != 0))
				throw new IllegalStateException(
						"Map must be initialized (by creating a tile at 0,0) before other tiles may be created.");

			Location l = Location.of(x, y);
			if (tilemap.containsKey(l))
				throw new IllegalStateException("A tile already exists at that position.");

			// TODO (Document that) subclasses of WildernessMap should only create their
			// chosen type of WildernessTile on that map.

			// This problem could be pushed around by grabbing the class type of the origin
			// tile and using that to sanitize tile constructions on the map, however, the
			// provided tile may not always be exactly of the type W (it may be a subtype).
			tilemap.put(location = l, (W) this);

			for (var v : LinkType.AdjacencyLink.list()) {
				var other = tilemap.get(v.travelLink(l));
				if (other != null) {
					linkedTiles.put(v, other);
					((WildernessTile) other).linkedTiles.put(v.opposite(), (W) this);
				}
			}
		}

		@SuppressWarnings("unchecked")
		public W getLinkedTile(LinkType link) {
			return linkedTiles.containsKey(link) ? linkedTiles.get(link) : generateTile((W) this, link);
		}

		protected final WildernessMap<W> getMap() {
			return WildernessMap.this;
		}

	}

	/**
	 * Generates a new {@link WildernessTile} that is linked to the
	 * <code>from</code> tile by the specified <code>link</code>. The
	 * {@link WildernessTile} class's constructor automatically links adjacent tiles
	 * by {@link AdjacencyLink} when constructed, but other link types will need to
	 * be
	 * 
	 * @param from The old tile that the new tile will be linked to.
	 * @param link The link from the old tile to the new tile.
	 * @return The new tile.
	 */
	protected abstract W generateTile(W from, LinkType link);

	public W getLinkedTile(W from, LinkType link) {
		return from.getLinkedTile(link);
	}

}
