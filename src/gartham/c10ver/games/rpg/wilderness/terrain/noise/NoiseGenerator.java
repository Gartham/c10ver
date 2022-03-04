package gartham.c10ver.games.rpg.wilderness.terrain.noise;

import gartham.c10ver.games.rpg.wilderness.Location;

public interface NoiseGenerator {
	/**
	 * <p>
	 * Generates a noise map for the given location based on the given "slice" or
	 * "selection" from the location. The size parameters define the size of each
	 * tile (and the spacing of each randomly generated influential point) while the
	 * start and end parameters define the "section" of the tile that will be
	 * returned. A tile may be 1000x1000, leading to very slow transitions between
	 * two indices in the returned grid, but only the indices in the rectangle from
	 * 40x40 to 80x80 may wish to be returned. When a slice like this is desired,
	 * the start and end parameters may be used to specify the slice.
	 * </p>
	 * 
	 * @param tileLocation The location of the tile in coordinate space.
	 * @param xStart       The starting x point of the section returned.
	 * @param yStart       The starting y point of the section returned.
	 * @param xEnd         The ending x point of the section returned.
	 * @param yEnd         the ending y point of the section returned.
	 * @param xSize        The width of a tile.
	 * @param ySize        The height of a tile.
	 * @return The generated noisemap.
	 */
	double[][] noisemap(Location tileLocation, int xStart, int yStart, int xEnd, int yEnd, int xSize, int ySize);
}
