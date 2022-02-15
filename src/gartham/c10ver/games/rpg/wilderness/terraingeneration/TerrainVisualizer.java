package gartham.c10ver.games.rpg.wilderness.terraingeneration;

import javafx.application.Application;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class TerrainVisualizer extends Application {

	private static final int CHUNKSIZE = 30, WIDTH_IN_CHUNKS = 60, HEIGHT_IN_CHUNKS = 60;
	private static final WritableImage IMAGE = new WritableImage(CHUNKSIZE * WIDTH_IN_CHUNKS,
			CHUNKSIZE * HEIGHT_IN_CHUNKS);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		primaryStage.show();
	}
}
