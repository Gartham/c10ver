package gartham.c10ver.games.rpg.wilderness;

public class CloverWildernessMap extends WildernessMap<CloverWildernessMap.CloverWildernessTile> {
	{
		new CloverWildernessTile();
	}

	public final class CloverWildernessTile extends WildernessTileBase<CloverWildernessTile> {

		private CloverWildernessTile(int x, int y, int width, int height) {
			super(CloverWildernessMap.this, x, y, width, height);
		}

		private CloverWildernessTile(int x, int y) {
			super(CloverWildernessMap.this, x, y);
		}

		private CloverWildernessTile() {
			super(CloverWildernessMap.this, 0, 0, 23, 31);
			getGraphix().add(new ExitGraphic());
		}

	}

	@Override
	protected CloverWildernessTile generateTile(CloverWildernessTile from, LinkType link) {
		// TODO Auto-generated method stub
		return null;
	}
}
