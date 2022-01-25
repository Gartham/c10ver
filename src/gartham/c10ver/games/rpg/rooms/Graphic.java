package gartham.c10ver.games.rpg.rooms;

public interface Graphic {
	/**
	 * Renders this {@link Graphic} onto the provided map.
	 * 
	 * @param map The map to render onto.
	 */
	void render(String[][] map);
}