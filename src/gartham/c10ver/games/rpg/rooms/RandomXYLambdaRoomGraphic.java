package gartham.c10ver.games.rpg.rooms;

import java.util.Random;

public abstract class RandomXYLambdaRoomGraphic implements XYLambdaRoomGraphic {
	private final long seed;
	private Random rand;

	public RandomXYLambdaRoomGraphic(long seed) {
		this.seed = seed;
	}

	@Override
	public void render(String[][] map) {
		rand = new Random(seed);
		XYLambdaRoomGraphic.super.render(map);
		rand = null;
	}

	@Override
	public String render(int x, int y) {
		return render(x, y, rand);
	}

	public abstract String render(int x, int y, Random rand);

}
