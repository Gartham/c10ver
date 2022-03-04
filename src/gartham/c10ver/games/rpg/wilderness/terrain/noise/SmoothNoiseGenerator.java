package gartham.c10ver.games.rpg.wilderness.terrain.noise;

import gartham.c10ver.games.rpg.wilderness.Location;

public class SmoothNoiseGenerator implements NoiseGenerator {

	private final GradGenerator gg;

	public SmoothNoiseGenerator(GradGenerator gg) {
		this.gg = gg;
	}

	public SmoothNoiseGenerator(long seed) {
		this(GradGenerator.continuous(seed));
	}

	@Override
	public double[][] noisemap(Location tileLocation, int xStart, int yStart, int xEnd, int yEnd, int xSize,
			int ySize) {

		return generateTile(xSize, ySize, gg, tileLocation.getX(), tileLocation.getY(), xStart, yStart, xEnd, yEnd);

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

		double result = (topYPos - y) / (topYPos - bottomYPos) * interpXY1
				+ (y - bottomYPos) / (topYPos - bottomYPos) * interpXY2;
		return (result);
	}

	private static double[][] generateTile(int tileWidth, int tileHeight, GradGenerator gg, int tileX, int tileY,
			int xPixStart, int yPixStart, int xPixEnd, int yPixEnd) {

		Vec tl = gg.generate(tileX, tileY);
		Vec tr = gg.generate(tileX + 1, tileY);
		Vec bl = gg.generate(tileX, tileY + 1);
		Vec br = gg.generate(tileX + 1, tileY + 1);

		double[][] result = new double[xPixEnd - xPixStart][yPixEnd - yPixStart];

		for (int pixX = xPixStart; pixX < xPixEnd; pixX++) {
			for (int pixY = yPixStart; pixY < yPixEnd; pixY++) {
				// Pixel gets 4 vecs.

				// Get point vector from each anchor.
				double ix = (pixX % tileWidth / (double) tileWidth);
				double ip = (ix);// + (1 / 2d / chunksize);
				double jx = (pixY % tileHeight / (double) tileHeight);
				double jp = (jx);// + (1 / 2d / chunksize);
				Vec dtl = new Vec(ip, jp);
				Vec dtr = new Vec(ip - 1, jp);
				Vec dbl = new Vec(ip, jp - 1);
				Vec dbr = new Vec(ip - 1, jp - 1);

				double atl = tl.dot(dtl);
				double atr = tr.dot(dtr);
				double abl = bl.dot(dbl);
				double abr = br.dot(dbr);

				double x =
//						(atl + atr + abl + abr) / 4;
						bilinearlyInterpolate(abl, abr, atl, atr, 1, 0, 1, 0, fade(ix), fade(jx));
//						interp(jx, interp(ix, atl, atr), interp(ix, abl, abr));

//				x = fade(x);

//				if (x < -1 || x > 1)
//					System.out.println(x);

				result[pixX][pixY] = Math.min(1, Math.max(-1, x));

				// Scale for color.
//				x += 1;
//				x /= 2;
//				Color color = Color.gray(Math.max(Math.min(1, x), 0));
//
//				IMAGE.getPixelWriter().setColor(i, j, color);
			}
		}

		return result;
	}

}
