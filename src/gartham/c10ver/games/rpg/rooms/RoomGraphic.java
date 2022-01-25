package gartham.c10ver.games.rpg.rooms;

public interface RoomGraphic {
	/**
	 * Renders this {@link RoomGraphic} onto the provided map.
	 * 
	 * @param map The map to render onto.
	 */
	void render(String[][] map);
}