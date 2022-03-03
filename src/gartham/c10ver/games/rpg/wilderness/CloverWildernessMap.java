package gartham.c10ver.games.rpg.wilderness;

import java.security.SecureRandom;
import java.util.Random;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.rooms.RandomXYLambdaRoomGraphic;
import gartham.c10ver.games.rpg.wilderness.LinkType.AdjacencyLink;
import gartham.c10ver.games.rpg.wilderness.terrain.Seed;
import gartham.c10ver.games.rpg.wilderness.terrain.SmoothBiomeShader;

public class CloverWildernessMap extends WildernessMap<CloverWildernessMap.CloverWildernessTile> {
	{
		new CloverWildernessTile();
	}

	public static final byte DEFAULT_TILE_SIZE = 23;
	private static final String BROWN = "\uD83D\uDFEB", YELLOW = "\uD83D\uDFE8", ORANGE = "\uD83D\uDFE7",
			RED = "\uD83D\uDFE5";

	private final long seed = JavaTools.bytesToLong(SecureRandom.getSeed(8));
	private final SmoothBiomeShader biomeShader = new SmoothBiomeShader();

	public final class CloverWildernessTile extends WildernessTileBase<CloverWildernessTile> {

		private CloverWildernessTile(int x, int y, int width, int height) {
			super(CloverWildernessMap.this, x, y, width, height);
		}

		private CloverWildernessTile(int x, int y) {
			super(CloverWildernessMap.this, x, y, DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
		}

		private CloverWildernessTile(Location loc, int width, int height) {
			super(CloverWildernessMap.this, loc.getX(), loc.getY(), width, height);
		}

		private CloverWildernessTile(Location loc) {
			super(CloverWildernessMap.this, loc.getX(), loc.getY(), DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
		}

		private CloverWildernessTile() {
			super(CloverWildernessMap.this, 0, 0, DEFAULT_TILE_SIZE, DEFAULT_TILE_SIZE);
			getGraphix().add(new ExitGraphic());
			getGraphix().add(centerCircleGraphic());
		}

		private long getTileXShift() {
			return getX() * DEFAULT_TILE_SIZE;
		}

		private long getTileYShift() {
			return getY() * DEFAULT_TILE_SIZE;
		}

		private final RandomXYLambdaRoomGraphic centerCircleGraphic() {
			return new RandomXYLambdaRoomGraphic(seed + getLocation().hashCode()) {
				@Override
				public String render(int x, int y, Random rand) {
					// Shift to "center" by adding half of DEFAULT_TILE_SIZE, since we want the
					// circle to be centered on the starting tile.
					double x0 = x + getTileXShift() - DEFAULT_TILE_SIZE / 2,
							y0 = y + getTileYShift() - DEFAULT_TILE_SIZE / 2;
					double rad = Math.sqrt(x0 * x0 + y0 * y0);
					return x0 == 0 && y0 == 0 ? null
							: rad < 28 ? rad > 20 || rad * rand.nextDouble() > 4 ? BROWN : switch (rand.nextInt(3)) {
					case 0 -> RED;
					case 1 -> YELLOW;
					case 2 -> ORANGE;
					default -> throw new IllegalArgumentException("Unexpected value: " + rand.nextInt(3));
					} : rad < 32 && rand.nextInt((int) (rad - 27)) == 0 ? BROWN : null;
				}
			};
		}

	}

	@Override
	protected CloverWildernessTile generateTile(CloverWildernessTile from, LinkType link) {

		if (link instanceof AdjacencyLink) {
			var cwt = new CloverWildernessTile(from.travel((AdjacencyLink) link));
//			cwt.getGraphix().add(map -> biomeShader.shade(map, new Seed(seed), cwt.getLocation()));
			if (cwt.getX() < 3 && cwt.getX() > -3 && cwt.getY() < 3 && cwt.getY() > -3)
				cwt.getGraphix().add(cwt.centerCircleGraphic());
			return cwt;
		} else// Implement other link types.
			throw new UnsupportedOperationException();
	}
}
