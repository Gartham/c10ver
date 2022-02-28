package gartham.c10ver.games.rpg.wilderness.terrain;

import java.util.Arrays;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TerrainVisualizer extends Application {

	private static final int CHUNKSIZE = 30, WIDTH_IN_CHUNKS = 30, HEIGHT_IN_CHUNKS = 30;
	private static final WritableImage IMAGE = new WritableImage(CHUNKSIZE * WIDTH_IN_CHUNKS,
			CHUNKSIZE * HEIGHT_IN_CHUNKS);
	private static final double MULTIPLIER = 1;

	interface GradGenerator {
		Vec generate(int x, int y);

		static GradGenerator continuous(long seed) {
			Random r = new Random(seed);
			return (x, y) -> {
				double sqr = r.nextDouble() * 2;
				return new Vec((r.nextBoolean() ? -1 : 1) * Math.sqrt(sqr),
						(r.nextBoolean() ? -1 : 1) * Math.sqrt(2 - sqr));
			};
		}

		Vec TL = new Vec(-1, 1), TR = new Vec(1, 1), BR = new Vec(1, -1), BL = new Vec(-1, -1);

		static GradGenerator discrete(long seed) {
			Random r = new Random(seed);
			return (x, y) -> {
				return r.nextBoolean() ? r.nextBoolean() ? TL : TR : r.nextBoolean() ? BR : BL;
			};
		}

	}

	public static void main(String[] args) {
		launch(args);
	}

	private static final class Vec {
		public double x, y;

		public Vec(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double dot(Vec other) {
			return x * other.x + y * other.y;
		}

		@Override
		public String toString() {
			return "[" + x + ", " + y + ']';
		}

		public double mag() {
			return Math.sqrt(x * x + y * y);
		}

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		long seed = 9;

		GradGenerator gg = GradGenerator.continuous(seed);

		for (int x = 0; x < WIDTH_IN_CHUNKS; x++)
			for (int y = 0; y < HEIGHT_IN_CHUNKS; y++) {
				double[][] r = generateTile(CHUNKSIZE, gg, seed)
//						, g = generateTile(CHUNKSIZE, gg, 257987198)
//						, b = generateTile(CHUNKSIZE, gg, -2851)
				;

				for (int i = 0; i < r.length; i++) {
					for (int j = 0; j < r[i].length; j++)
						IMAGE.getPixelWriter().setColor(x * CHUNKSIZE + i, y * CHUNKSIZE + j,
								Color.gray(clampForColor(r[i][j])));
				}
			}

		primaryStage.show();

		primaryStage.setScene(new Scene(new ScrollPane(new ImageView(IMAGE))));

	}

	private static double clampForColor(double noise) {
		// Noise ranges from -1 to 1.
		return (noise + 1) / 2;
	}

	public static double[][] generateTile(int chunksize, GradGenerator gg, double gradMultiplier) {
		Vec[][] grads = new Vec[WIDTH_IN_CHUNKS + 1][HEIGHT_IN_CHUNKS + 1];

		for (int i = 0; i <= WIDTH_IN_CHUNKS; i++) {
			for (int j = 0; j <= HEIGHT_IN_CHUNKS; j++) {
				grads[i][j] = gg.generate(i, j);
				grads[i][j].x *= gradMultiplier;
				grads[i][j].y *= gradMultiplier;
			}
		}

		double[][] result = new double[chunksize][chunksize];

		for (int i = 0; i < chunksize; i++) {
			for (int j = 0; j < chunksize; j++) {
				// Pixel gets 4 vecs.
				Vec tl = grads[i / chunksize][j / chunksize];
				Vec tr = grads[i / chunksize + 1][j / chunksize];
				Vec bl = grads[i / chunksize][j / chunksize + 1];
				Vec br = grads[i / chunksize + 1][j / chunksize + 1];

				// Get point vector from each anchor.
				double ix = fade(i % chunksize / (double) chunksize);
				double ip = ix + (1 / 2d / chunksize);
				double jx = fade(j % chunksize / (double) chunksize);
				double jp = jx + (1 / 2d / chunksize);
				Vec dtl = new Vec(ip, jp);
				Vec dtr = new Vec(1 - ip, jp);
				Vec dbl = new Vec(ip, 1 - jp);
				Vec dbr = new Vec(1 - ip, 1 - jp);

				double atl = tl.dot(dtl);
				double atr = tr.dot(dtr);
				double abl = bl.dot(dbl);
				double abr = br.dot(dbr);

				double x =
//						(atl + atr + abl + abr) / 4;
						bilinearlyInterpolate(abl, abr, atl, atr, 1, 0, 1, 0, ix, jx);

//				x = fade(x);

//				if (x < -1 || x > 1)
//					System.out.println(x);

				result[i][j] = Math.min(1, Math.max(-1, x));

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

	private static double fade(double x) {
		return x * x * x * (10 + x * (-15 + 6 * x));
	}

	private static double bilinearlyInterpolate(double bottomLeft, double bottomRight, double topLeft, double topRight,
			double rightXPos, double leftXPos, double bottomYPos, double topYPos, double x, double y) {
		double interpXY1 = (rightXPos - x) / (rightXPos - leftXPos) * bottomLeft
				+ (x - leftXPos) / (rightXPos - leftXPos) * bottomRight;
		double interpXY2 = (rightXPos - x) / (rightXPos - leftXPos) * topLeft
				+ (x - leftXPos) / (rightXPos - leftXPos) * topRight;

//		interpXY1 = (interpXY1);
//		interpXY2 = (interpXY2);

		double result = (topYPos - y) / (topYPos - bottomYPos) * interpXY1
				+ (y - bottomYPos) / (topYPos - bottomYPos) * interpXY2;
		return (result);
	}
}
