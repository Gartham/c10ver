package gartham.c10ver.games.rpg.wilderness;

import org.alixia.javalibrary.JavaTools;

public final class Location {
	private final int x, y;

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Location && ((Location) obj).x == x && ((Location) obj).y == y;
	}

	@Override
	public int hashCode() {
		return JavaTools.hash(x, y);
	}

	public final static Location of(int x, int y) {
		return new Location(x, y);
	}
}