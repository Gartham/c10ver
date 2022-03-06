package gartham.c10ver.games.rpg.wilderness.terrain.noise;

import gartham.c10ver.games.rpg.wilderness.Location;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TerrainVisualizer extends Application {

	private static final int CHUNKSIZE = 70, WIDTH_IN_CHUNKS = 10, HEIGHT_IN_CHUNKS = 10;
	private static final WritableImage IMAGE = new WritableImage(CHUNKSIZE * WIDTH_IN_CHUNKS,
			CHUNKSIZE * HEIGHT_IN_CHUNKS);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		NoiseGenerator ng = new SmoothNoiseGenerator(4722);

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				var nm = ng.noisemap(Location.of(x, y), 0, 0, 200, 200, 200, 200);
				for (int i = 0; i < nm.length; i++) {
					for (int j = 0; j < nm[i].length; j++)
						IMAGE.getPixelWriter().setColor(y * 200 + i, x * 200 + j,
								Color.hsb(nm[i][j] * 180, 1, Math.min(1, Math.max(0, clampForColor(nm[i][j])))));
				}
			}
		}

		primaryStage.show();

		ImageView view = new ImageView(IMAGE);
		primaryStage.setScene(new Scene(new ScrollPane(view)));

	}

	private static double clampForColor(double noise) {
		// Noise ranges from -1 to 1.
		return (noise + 1) / 2;
	}
}
