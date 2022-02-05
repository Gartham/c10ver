package gartham.c10ver.games.rpg.wilderness;

import java.security.SecureRandom;
import java.util.Random;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.wilderness.LinkType.AdjacencyLink;

public class CloverWildernessMap extends WildernessMap<CloverWildernessMap.CloverWildernessTile> {
	{
		new CloverWildernessTile();
	}

	private final long seed = JavaTools.bytesToLong(SecureRandom.getSeed(8));

	public final class CloverWildernessTile extends WildernessTileBase<CloverWildernessTile> {

		private CloverWildernessTile(int x, int y, int width, int height) {
			super(CloverWildernessMap.this, x, y, width, height);
		}

		private CloverWildernessTile(int x, int y) {
			super(CloverWildernessMap.this, x, y);
		}

		private CloverWildernessTile(Location loc, int width, int height) {
			super(CloverWildernessMap.this, loc.getX(), loc.getY(), width, height);
		}

		private CloverWildernessTile(Location loc) {
			super(CloverWildernessMap.this, loc.getX(), loc.getY());
		}

		private CloverWildernessTile() {
			super(CloverWildernessMap.this, 0, 0, 23, 23);
			getGraphix().add(new ExitGraphic());
		}

	}

	@Override
	protected CloverWildernessTile generateTile(CloverWildernessTile from, LinkType link) {
		Random rand = new Random(seed);

		if (link instanceof AdjacencyLink) {
			var cwt = new CloverWildernessTile(from.travel((AdjacencyLink) link));

		} else
			throw new UnsupportedOperationException();
	}
}
