package gartham.c10ver.games.rpg.wilderness.terrain;

import gartham.c10ver.games.rpg.wilderness.WildernessTileBase;

public interface TerrainObject<T extends WildernessTileBase<T>> {
	void render(String[][] tilemap, T tile, int localX, int localY);
}
