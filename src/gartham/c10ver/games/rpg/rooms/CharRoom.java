package gartham.c10ver.games.rpg.rooms;

public interface CharRoom {
	/**
	 * Returns a <code>char[][]</code> of the layout of the {@link Room}. The first
	 * dimension (i.e., <code>layout()[x]</code>) indexes the rows, i.e., each line
	 * of the room (AKA the y-axis), with index <code>0</code> referring to the top
	 * of the room. The second dimension indexes the columns of the room, with the
	 * first index (i.e. index <code>0</code>) being the leftmost value. The result
	 * of this method is a matrix (i.e. it is not jagged).
	 * 
	 * @return The layout, as a 2D char array.
	 */
	public char[][] tilemap();

	/**
	 * Returns a new {@link String} array of each row of the layout contained in one
	 * string, starting with the topmost row (i.e. <code>layout()[0]</code>) as the
	 * first element in (i.e. <code>layoutLines()[0]</code>). The length of the
	 * resulting matrix is the same size as <code>{@link #tilemap()}.length</code>,
	 * and the length of each of the {@link String}s contained in the returned
	 * <code>String[]</code> is the length of any of the 1D arrays contained in the
	 * matrix returned by {@link #tilemap()}.
	 * 
	 * @return A string array, composed from the separate char arrays held in the
	 *         char matrix returned from {@link #tilemap()}.
	 */
	default String[] tilemapLines() {
		var layout = tilemap();
		var lines = new String[layout.length];
		for (int i = 0; i < layout.length; i++)
			lines[i] = new String(layout[i]);
		return lines;
	}

	/**
	 * Returns a printable map of the {@link Room}. The result is simply the
	 * {@link String}s in the value returned from {@link #tilemapLines()}, joined
	 * together with the newline character as the delimiter.
	 * 
	 * @return A printable, {@link String} version of the room.
	 */
	default String tilemapString() {
		return String.join("\n", tilemapLines());
	}
}
