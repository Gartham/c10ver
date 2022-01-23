package gartham.c10ver.games.rpg.wilderness;

import java.util.HashMap;
import java.util.Map;

import org.alixia.javalibrary.JavaTools;

public class WildernessMap {

	private final Map<Location, WildernessTile> tilemap = new HashMap<>();
	private final WildernessTile origin = new WildernessTile();
	{
		tilemap.put(Location.of(0, 0), origin);
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

}
