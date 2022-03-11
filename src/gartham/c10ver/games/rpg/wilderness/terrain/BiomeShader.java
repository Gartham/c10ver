package gartham.c10ver.games.rpg.wilderness.terrain;

import gartham.c10ver.games.rpg.random.Seed;
import gartham.c10ver.games.rpg.wilderness.Location;

public interface BiomeShader {
	/**
	 * Renders biomes onto the given tile with the given {@link Seed}. The specified
	 * {@link Location} is used to perform location-dependent biome shading.
	 * 
	 * @param tile         The String grid to render the biome onto.
	 * @param seed         The seed to use for rendering.
	 * @param tileLocation The location of the tile in the tilemap.
	 */
	void shade(String[][] tile, Seed seed, Location tileLocation);
}
