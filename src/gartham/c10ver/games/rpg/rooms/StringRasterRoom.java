package gartham.c10ver.games.rpg.rooms;

public class StringRasterRoom extends RasterRoom<String> implements StringRoom {

	public StringRasterRoom(int width, int height) {
		super(height, width, String.class);
	}

	@SuppressWarnings("unchecked")
	public static StringRasterRoom discordSquare(int size) {
		return new StringRasterRoom((int) (2.2 * size), size);
	}

}
