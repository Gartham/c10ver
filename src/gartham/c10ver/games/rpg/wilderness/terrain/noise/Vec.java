package gartham.c10ver.games.rpg.wilderness.terrain.noise;

final class Vec {
	public double x, y;

	public Vec(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double dot(Vec other) {
		return x * other.x + y * other.y;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + ']';
	}

	public double mag() {
		return Math.sqrt(x * x + y * y);
	}

}