package gartham.c10ver.games.rpg.rooms;

/**
 * Represents a room. This class stores all of the information pertaining to a
 * room in a game.
 * 
 * @author Gartham
 *
 */
public interface Room<T> {
	
	public T[][] tilemap();

}
