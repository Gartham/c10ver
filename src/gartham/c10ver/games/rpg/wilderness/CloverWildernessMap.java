package gartham.c10ver.games.rpg.wilderness;

import java.security.SecureRandom;
import java.util.Random;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.rooms.XYLambdaRoomGraphic;
import gartham.c10ver.games.rpg.wilderness.LinkType.AdjacencyLink;

public class CloverWildernessMap extends WildernessMap<CloverWildernessMap.CloverWildernessTile> {
	{
		new CloverWildernessTile();
	}

	public static final byte DEFAULT_TILE_SIZE = 23;

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
		}

		private long getTileXShift() {
			return getX() * DEFAULT_TILE_SIZE;
		}

		private long getTileYShift() {
			return getY() * DEFAULT_TILE_SIZE;
		}

	}

	@Override
	protected CloverWildernessTile generateTile(CloverWildernessTile from, LinkType link) {
		Random rand = new Random(seed);

		if (link instanceof AdjacencyLink) {
			var cwt = new CloverWildernessTile(from.travel((AdjacencyLink) link));
			cwt.getGraphix().add((XYLambdaRoomGraphic) (x,
					y) -> x - 12 >= Math.sin(y / 3 + rand.nextDouble() * Math.PI * 2) * 3 ? "\uD83D\uDFE5" : null);
			return cwt;
		} else
			throw new UnsupportedOperationException();
	}
}
