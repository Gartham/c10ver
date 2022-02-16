package gartham.c10ver.games.rpg.wilderness;

import java.security.SecureRandom;
import java.util.Random;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.rooms.RandomXYLambdaRoomGraphic;
import gartham.c10ver.games.rpg.wilderness.LinkType.AdjacencyLink;

public class CloverWildernessMap extends WildernessMap<CloverWildernessMap.CloverWildernessTile> {
	{
		new CloverWildernessTile();
	}

	public static final byte DEFAULT_TILE_SIZE = 23;
	private static final String BROWN = "\uD83D\uDFEB", YELLOW = "\uD83D\uDFE8", ORANGE = "\uD83D\uDFE7",
			RED = "\uD83D\uDFE5";

	private final long seed = JavaTools.bytesToLong(SecureRandom.getSeed(8));

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
					if (x0 == 0 && y0 == 0)
						return null;
					else if (rad < 28)
						if (rad > 20 || rad * rand.nextDouble() > 4)
							return BROWN;
						else
							return switch (rand.nextInt(3)) {
							case 0 -> RED;
							case 1 -> YELLOW;
							case 2 -> ORANGE;
							default -> throw new IllegalArgumentException("Unexpected value: " + rand.nextInt(3));
							};
					else if (rad < 32 && rand.nextInt((int) (rad - 27)) == 0)
						return BROWN;
					else
						return null;
				}
			};
		}

	}

	@Override
	protected CloverWildernessTile generateTile(CloverWildernessTile from, LinkType link) {

		if (link instanceof AdjacencyLink) {
			var cwt = new CloverWildernessTile(from.travel((AdjacencyLink) link));
			if (cwt.getX() < 3 && cwt.getX() > -3 && cwt.getY() < 3 && cwt.getY() > -3)
				cwt.getGraphix().add(cwt.centerCircleGraphic());
			else {

			}
			return cwt;
		} else
			throw new UnsupportedOperationException();
	}
}
