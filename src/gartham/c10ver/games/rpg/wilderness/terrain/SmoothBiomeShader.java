package gartham.c10ver.games.rpg.wilderness.terrain;

import java.util.Random;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.wilderness.Emoji;
import gartham.c10ver.games.rpg.wilderness.Location;

public class SmoothBiomeShader implements BiomeShader {

	private final Emoji[] emojis = Emoji.values();

	private final class Vec {
		private final double x, y;

		public Vec(double x, double y) {
			this.x = x;
			this.y = y;
		}

	}

	@Override
	public void shade(String[][] tile, Seed seed, Location tileLocation) {
//		Vec[][] grads = new Vec[tile.length + 1][tile[0].length + 1];
//
//		for (int i = 0; i < grads.length; i++)
//			for (int j = 0; j < grads.length; j++) {
//				double sqr = r.nextDouble();
//				grads[i][j] = new Vec(Math.sqrt(sqr), Math.sqrt(1 - sqr));
//			}

		double tl = new Random(seed.getSeed() + JavaTools.hash(tileLocation.getX(), tileLocation.getY())).nextDouble(),
				tr = new Random(seed.getSeed() + JavaTools.hash(tileLocation.getX() + 1, tileLocation.getY()))
						.nextDouble(),
				bl = new Random(seed.getSeed() + JavaTools.hash(tileLocation.getX(), tileLocation.getY() - 1))
						.nextDouble(),
				br = new Random(seed.getSeed() + JavaTools.hash(tileLocation.getX() + 1, tileLocation.getY() - 1))
						.nextDouble();
		// These values are not random wrt each other, because their seeds are not random wrt each other.
		// The result of each call is almost always very close of the result of each other.
		// Uncomment below and execute this (through some api or smth) to experience this.
//		System.out.println("[" + tileLocation + "]: " + tl + ", " + tr + ", " + br + ", " + bl);

		// Bilinearly interpolate between randomly generated points.

		for (int i = 0; i < tile.length; i++) {
			for (int j = 0; j < tile[i].length; j++) {
				double res = bilinearlyInterpolate(bl, br, tl, tr, tile[0].length, 0, 0, tile.length, j, i);
				tile[i][j] = emojis[(int) (res * emojis.length)].getValue();
			}
		}
	}

	private static double bilinearlyInterpolate(double bottomLeft, double bottomRight, double topLeft, double topRight,
			double rightXPos, double leftXPos, double bottomYPos, double topYPos, double x, double y) {
		double interpXY1 = (rightXPos - x) / (rightXPos - leftXPos) * bottomLeft
				+ (x - leftXPos) / (rightXPos - leftXPos) * bottomRight;
		double interpXY2 = (rightXPos - x) / (rightXPos - leftXPos) * topLeft
				+ (x - leftXPos) / (rightXPos - leftXPos) * topRight;

		double result = (topYPos - y) / (topYPos - bottomYPos) * interpXY1
				+ (y - bottomYPos) / (topYPos - bottomYPos) * interpXY2;
		return result;
	}

}
