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

//		System.out.println("[(" + tileX + ", " + xPixStart / (tileWidth / (xPixEnd - xPixStart)) + "), (" + tileY + ", "
//				+ yPixStart / (tileHeight / (yPixEnd - yPixStart)) + ")]\n\n");

		// +X is to the right, -X is to the left.
		// +Y is up, -Y is down.
		Vec bl = gg.generate(Location.of(tileX, tileY), seed);// Bottom left is the origin.
		Vec br = gg.generate(Location.of(tileX + 1, tileY), seed);// Bottom right is 1, 0 from origin.
		Vec tl = gg.generate(Location.of(tileX, tileY + 1), seed);// Top left is 0, 1 from origin.
		Vec tr = gg.generate(Location.of(tileX + 1, tileY + 1), seed); // Top right is 1,1 from origin.

		double[][] result = new double[xPixEnd - xPixStart][yPixEnd - yPixStart];

//		{
//			double ix = xPixStart / (double) tileWidth, jx = yPixStart / (double) tileHeight;
//			double atl = tl.dot(new Vec(ix, jx - 1));
//			double atr = tr.dot(new Vec(ix - 1, jx - 1));
//			double abl = bl.dot(new Vec(ix, jx));
//			double abr = br.dot(new Vec(ix - 1, jx));
//		}

		// We fill the 2D array from left to right (one 1D array at a time, increasing
		// in the X direction) then from top to bottom (decreasing in the Y direction).
		for (int pixX = xPixStart; pixX < xPixEnd; pixX++) {
			for (int pixY = yPixEnd - 1; pixY >= yPixStart; pixY--) {

				double ix = pixX / (double) tileWidth, jx = pixY / (double) tileHeight;
//				We dot each corner vector with the vector of this point from that corner.
				double atl = tl.dot(new Vec(ix, jx - 1));
				double atr = tr.dot(new Vec(ix - 1, jx - 1));
				double abl = bl.dot(new Vec(ix, jx));
				double abr = br.dot(new Vec(ix - 1, jx));

				double x = bilinearlyInterpolate(abl, abr, atl, atr, 1, 0, 1, 0, fade(ix), fade(jx));


				result[pixX - xPixStart][pixY - yPixStart] = Math.min(1, Math.max(-1, x));

			}
		}

		return result;
	}

}
