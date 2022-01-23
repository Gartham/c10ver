package gartham.c10ver.games.rpg.wilderness;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alixia.javalibrary.JavaTools;

public class WildernessMap {

	private final Map<Location, WildernessTile> tilemap = new HashMap<>();
	private final WildernessTile origin;

	protected WildernessMap(WildernessTile origin) {
		this.origin = origin;
	}

	public WildernessTile getOrigin() {
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

	public WildernessTile get(int x, int y) {
		return tilemap.get(new Location(x, y));
	}

	public abstract class WildernessTile {
		private final Map<LinkType, WildernessTile> linkedTiles = new HashMap<>(2);
		private final Location location;

		public Location getLocation() {
			return location;
		}

		public Map<LinkType, WildernessTile> getLinkedTiles() {
			return Collections.unmodifiableMap(linkedTiles);
		}

		public WildernessTile get(LinkType link) {
			return linkedTiles.get(link);
		}

		private WildernessTile(int x, int y) {
			tilemap.put(location = Location.of(x, y), this);
		}

		public WildernessTile go(LinkType link) {
			return linkedTiles.containsKey(link) ? linkedTiles.get(link) : generateTile(link);
		}

		// TODO Contains logic for generating a tile at the new linked location.
		protected abstract WildernessTile generateTile(LinkType link);
	}

}
