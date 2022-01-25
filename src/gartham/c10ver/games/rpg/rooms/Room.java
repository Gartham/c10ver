package gartham.c10ver.games.rpg.rooms;

/**
 * Represents a room. This class stores all of the information pertaining to a
 * room in a game.
 * 
 * @author Gartham
 *
 */
public interface Room<T> {

	/**
	 * Returns a tilemap of this {@link Room}. The tilemap does not need to be
	 * "linked" to this room (in that any changes to the tilemap are reflected by
	 * the {@link Room} object), nor does it need not to be. If a {@link Room} type
	 * specifies a <code>height</code> and <code>width</code>, then that
	 * {@link Room}'s <code>height</code> corresponds to the first dimension of the
	 * 2D array returned by its {@link #tilemap()} method, and the
	 * <code>width</code> corresponds to the second dimension. Indexing is performed
	 * from the top left, to the bottom right. The terms <code>depth</code> and
	 * <code>y</code> correspond to indexing the <code>height</code>, with a depth
	 * of <code>0</code> or <code>y=0</code> referring to the topmost row of the 2D
	 * array returned by {@link #tilemap()}. The terms <code>breadth</code> and
	 * <code>x</code> correspond to indexing the <code>width</code>, with a breadth
	 * of <code>0</code> or <code>x=0</code> referring to the leftmost column of the
	 * 2D array returned by {@link #tilemap()}. Indexing is of the form:
	 * <code>tilemap()[depth][breadth]</code> or <code>tilemap()[y][x]</code>.
	 * 
	 * @return A tilemap array which represents this {@link Room} at the time of
	 *         calling and at least until the {@link Room} object undergoes
	 *         modifications that occur after the call to {@link #tilemap()}.
	 */
	public T[][] tilemap();

}
