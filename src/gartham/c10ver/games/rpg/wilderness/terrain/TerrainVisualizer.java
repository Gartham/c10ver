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

	public static void main(String[] args) {
		launch(args);
	}

	private final class Vec {
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

		Vec[][] grads = new Vec[WIDTH_IN_CHUNKS + 1][HEIGHT_IN_CHUNKS + 1];

		long seed = 8;
		Random r = new Random(seed);

		for (int i = 0; i <= WIDTH_IN_CHUNKS; i++) {
			for (int j = 0; j <= HEIGHT_IN_CHUNKS; j++) {
				double sqr = r.nextDouble() * 2;
				grads[i][j] = new Vec((r.nextBoolean() ? -1 : 1) * Math.sqrt(sqr),
						(r.nextBoolean() ? -1 : 1) * Math.sqrt(2 - sqr));
				grads[i][j].x *= MULTIPLIER;
				grads[i][j].y *= MULTIPLIER;
			}
		}

		for (int i = 0; i < CHUNKSIZE * WIDTH_IN_CHUNKS; i++) {
			for (int j = 0; j < CHUNKSIZE * HEIGHT_IN_CHUNKS; j++) {
				// Pixel gets 4 vecs.
				Vec tl = grads[i / CHUNKSIZE][j / CHUNKSIZE];
				Vec tr = grads[i / CHUNKSIZE + 1][j / CHUNKSIZE];
				Vec bl = grads[i / CHUNKSIZE][j / CHUNKSIZE + 1];
				Vec br = grads[i / CHUNKSIZE + 1][j / CHUNKSIZE + 1];

				// Get point vector from each anchor.
				double ip = i % CHUNKSIZE / (double) CHUNKSIZE + (1 / 2d / CHUNKSIZE);
				double jp = j % CHUNKSIZE / (double) CHUNKSIZE + (1 / 2d / CHUNKSIZE);
				Vec dtl = new Vec(ip, jp);
				Vec dtr = new Vec(1 - ip, jp);
				Vec dbl = new Vec(ip, 1 - jp);
				Vec dbr = new Vec(1 - ip, 1 - jp);

				double atl = tl.dot(dtl);
				double atr = tr.dot(dtr);
				double abl = bl.dot(dbl);
				double abr = br.dot(dbr);

				double x = bilinearlyInterpolate(abl, abr, atl, atr, CHUNKSIZE, 0, CHUNKSIZE, 0, (i % CHUNKSIZE),
						(j % CHUNKSIZE));

				Color color = Color.gray(Math.max(Math.min(1, x), 0));

				IMAGE.getPixelWriter().setColor(i, j, color);
			}
		}

		primaryStage.show();

		primaryStage.setScene(new Scene(new ScrollPane(new ImageView(IMAGE))));

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
