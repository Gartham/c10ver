package gartham.c10ver.games.rpg.wilderness.terrain.noise;

import gartham.c10ver.games.rpg.random.Seed;
import gartham.c10ver.games.rpg.wilderness.Location;

public class SmoothNoiseGenerator implements NoiseGenerator {

	private final GradGenerator gg;

	public SmoothNoiseGenerator(GradGenerator gg) {
		this.gg = gg;
	}

	public SmoothNoiseGenerator(long seed) {
		this(GradGenerator.continuous());
	}

	@Override
	public double[][] noisemap(Seed seed, Location tileLocation, int xStart, int yStart, int xEnd, int yEnd, int xSize,
			int ySize) {

		return generateTile(seed, xSize, ySize, gg, tileLocation.getX(), tileLocation.getY(), xStart, yStart, xEnd,
				yEnd);

	}

	private static double fade(double x) {
		return x * x * x * (10 + x * (-15 + 6 * x));
	}

	private static double bilinearlyInterpolate(double bottomLeft, double bottomRight, double topLeft, double topRight,
			double rightXPos, double leftXPos, double bottomYPos, double topYPos, double x, double y) {
		double interpXY1 = (rightXPos - x) / (rightXPos - leftXPos) * bottomLeft
				+ (x - leftXPos) / (rightXPos - leftXPos) * bottomRight;
		double interpXY2 = (rightXPos - x) / (rightXPos - leftXPos) * topLeft
				+ (x - leftXPos) / (rightXPos - leftXPos) * topRight;

		// interpXY1 = (interpXY1);
		// interpXY2 = (interpXY2);

		return ((topYPos - y) / (topYPos - bottomYPos) * interpXY1
				+ (y - bottomYPos) / (topYPos - bottomYPos) * interpXY2);
	}

	private static double[][] generateTile(Seed seed, int tileWidth, int tileHeight, GradGenerator gg, int tileX,
			int tileY, int xPixStart, int yPixStart, int xPixEnd, int yPixEnd) {

		Vec tl = gg.generate(Location.of(tileX, tileY), seed);
		Vec tr = gg.generate(Location.of(tileX + 1, tileY), seed);
		Vec bl = gg.generate(Location.of(tileX, tileY + 1), seed);
		Vec br = gg.generate(Location.of(tileX + 1, tileY + 1), seed);

		double[][] result = new double[xPixEnd - xPixStart][yPixEnd - yPixStart];

		for (int pixX = xPixStart; pixX < xPixEnd; pixX++) {
			for (int pixY = yPixStart; pixY < yPixEnd; pixY++) {
				// Pixel gets 4 vecs.

				// Get point vector from each anchor.
				double ix = pixX % tileWidth / (double) tileWidth, jx = pixY % tileHeight / (double) tileHeight;
				double atl = tl.dot(new Vec(ix, jx));
				double atr = tr.dot(new Vec(ix - 1, jx));
				double abl = bl.dot(new Vec(ix, jx - 1));
				double abr = br.dot(new Vec(ix - 1, jx - 1));

				double x = bilinearlyInterpolate(abl, abr, atl, atr, 1, 0, 1, 0, fade(ix), fade(jx));

				result[pixX - xPixStart][pixY - yPixStart] = Math.min(1, Math.max(-1, x));

			}
		}

		return result;
	}

}
