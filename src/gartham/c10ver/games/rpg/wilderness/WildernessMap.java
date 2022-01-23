package gartham.c10ver.games.rpg.wilderness;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alixia.javalibrary.JavaTools;

public class WildernessMap<W extends gartham.c10ver.games.rpg.wilderness.WildernessMap<W>.WildernessTile> {

	private final Map<Location, W> tilemap = new HashMap<>();
	private final W origin;

	protected WildernessMap(W origin) {
		this.origin = origin;
	}

	public W getOrigin() {
		return origin;
	}

	private final static class Location {
		private final int x, y;

		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Location && ((Location) obj).x == x && ((Location) obj).y == y;
		}

		@Override
		public int hashCode() {
			return JavaTools.hash(x, y);
		}

		private final static Location of(int x, int y) {
			return new Location(x, y);
		}
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
		private WildernessTile(int x, int y) {
			// TODO (Document that) subclasses of WildernessMap should only create their
			// chosen type of WildernessTile on that map.

			// This problem could be pushed around by grabbing the class type of the origin
			// tile and using that to sanitize tile constructions on the map, however, the
			// provided tile may not always be exactly of the type W (it may be a subtype).
			tilemap.put(location = Location.of(x, y), (W) this);
		}

		public W go(LinkType link) {
			return linkedTiles.containsKey(link) ? linkedTiles.get(link) : generateTile(link);
		}

		// TODO Contains logic for generating a tile at the new linked location.
		protected abstract W generateTile(LinkType link);
	}

}
