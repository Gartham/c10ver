package gartham.c10ver.games.rpg.rooms;

public class RoomIcon implements RoomGraphic {

	private final String[][] icon;
	private final int d, b;
	/**
	 * <ul>
	 * <li>If <code>false</code>, {@link #render(String[][])} will function in
	 * "overlay" mode, where it won't copy values that are <code>null</code> from
	 * {@link #icon} into the destination map array to render to. This allows icons
	 * that are not perfectly rectangular to be "rendered on top" of other graphics
	 * or the background without the space in the crevices of their bounds
	 * overwriting pixels underneath.</li>
	 * <li>If <code>true</code>, {@link #render(String[][])} will function in
	 * "overwrite" mode, where it will copy values of {@link #icon} to the map array
	 * being rendered to, regardless of what the value is. This is usually faster
	 * than non-solid mode since it uses
	 * {@link System#arraycopy(Object, int, Object, int, int)}, rather than manually
	 * copying every array value in a loop.</li>
	 * </ul>
	 */
	private final boolean solid;

	public RoomIcon(String[][] icon, int d, int w, boolean solid) {
		this.icon = icon;
		this.d = d;
		this.b = w;
		this.solid = solid;
	}

	public RoomIcon(int d, int w, boolean solid, String[]... icon) {
		this.icon = icon;
		this.d = d;
		this.b = w;
		this.solid = solid;
	}

	public String[][] getIcon() {
		return icon;
	}

	public int getD() {
		return d;
	}

	public int getB() {
		return b;
	}

	public boolean isSolid() {
		return solid;
	}

	@Override
	public void render(String[][] map) {
		// Nulls are treated as no-op.
		if (solid)
			for (int i = d; i < icon.length + d; i++)
				System.arraycopy(icon[i - d], 0, map[i], w, icon[i].length);
		else
			for (int i = d; i < icon.length + d; i++)
				for (int j = w; j < icon[i].length + w; j++)
					map[i][j] = icon[i - d][j - w];
	}

}
