package gartham.c10ver.games.rpg.rooms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import gartham.c10ver.utils.Direction;

public class RectangularDungeonRoom implements StringRoom {

	// Vertical (Single, Double), Horizontal
	private static final String[] SIDE = { "\u2502", "\u2551", "\u2500", "\u2550" },
			// Top (Right, Left), Bottom
			CORNERS = { "\u2510", "\u250C", "\u2518", "\u2514" }, SPLITTERS = { "\u2534", // Horizontal Up
					"\u252C", // Horizontal Down
					"\u251C", // Vertical Right
					"\u2524" // Vertical Left
			};

	// vertical, horizontal - [3][0] is the fourth row and the first column.
	private final int width, height;

	public RectangularDungeonRoom(int width, int height) {
		if (width < 3 || height < 3)
			throw new IllegalArgumentException("Room can't be smaller than 3 units!");
		this.height = height;
		this.width = width;

	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	private final Set<Opening> openings = new HashSet<>();
	private final Set<RoomGraphic> graphics = new HashSet<>();

	public static class Image implements RoomGraphic {

		private String[][] image;
		private int breadth, depth;// "Width" coordinate, "height" coordinate

		private Image(String[][] image, int depth, int breadth) {
			this.image = image;
			this.breadth = breadth;
			this.depth = depth;
		}

		@Override
		public void render(String[][] map) {
			for (int i = 0; i < image.length; i++)
				System.arraycopy(image[i], 0, map[i + depth], breadth, image[i].length);

		}

	}

	public static class Opening {
		private final Direction direction;
		private final int gapwidth, position;

		public Direction getDirection() {
			return direction;
		}

		public int getGapwidth() {
			return gapwidth;
		}

		public int getPosition() {
			return position;
		}

		private Opening(Direction direction, int gapwidth, int position) {
			this.direction = direction;
			this.gapwidth = gapwidth;
			this.position = position;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((direction == null) ? 0 : direction.hashCode());
			result = prime * result + gapwidth;
			result = prime * result + position;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Opening other = (Opening) obj;
			if (direction != other.direction)
				return false;
			if (gapwidth != other.gapwidth)
				return false;
			if (position != other.position)
				return false;
			return true;
		}

	}

	/**
	 * <p>
	 * Creates an opening in this room on the wall of this room corresponding to the
	 * specified {@link Direction}. The width of the actual opening is the gap
	 * width; the gap width does not include the 2 characters around the opening
	 * that are used to convey that there is an opening. The position is how far
	 * down (for vertical sides, {@link Direction#RIGHT} or {@link Direction#LEFT})
	 * or how far to the right (for horizontal sides, {@link Direction#UP} or
	 * {@link Direction#DOWN}) the opening should be. The boundaries of the wall
	 * selected are taken into account when specifying the gap position and the
	 * position is clamped so that the opening will fit on the wall. If a gap is too
	 * large to fit in a wall, it is shrunk to fit. Clamping is prioritized such
	 * that if a wall of the specified gap-width cannot fit at the specified
	 * position, but can fit on the wall, it is moved so that it fits. (Position is
	 * mutated preferentially to gap-width.) Note that this method does not take
	 * previous calls (so previous gaps) into account, so previous gaps may be
	 * overwritten entirely or partially, leading to strange rendering anomalies if
	 * care is not taken by callers.
	 * </p>
	 * <p>
	 * Although the gapwidth does not include the two characters used to indicate
	 * that there is a gap in a wall, the position does. Furthermore, the position
	 * begins in the first non-corner tile, so a position of 0 will indicate that
	 * the splitter tile used to indicate that there is an opening for this opening
	 * will come immediately after the corner tile in the {@link RectangularDungeonRoom}.
	 * </p>
	 * 
	 * @param side     The {@link Direction} of the room to put an opening in the
	 *                 wall of.
	 * @param gapwidth The width of the opening. (The number of empty characters
	 *                 wide the opening should be.)
	 * @param pos      The shift from the top or right (depending on whether the
	 *                 wall runs vertically or horizontally, respectively) that the
	 *                 opening should be made.
	 */
	public Opening createOpening(Direction side, int gapwidth, int pos) {
		Opening opening = new Opening(side, gapwidth, pos);
		openings.add(opening);
		return opening;
	}

	public RoomGraphic createIcon(int depth, int breadth, String icon) {
		RoomGraphic gr = map -> map[depth][breadth] = icon;
		graphics.add(gr);
		return gr;
	}

	/**
	 * Creates an icon in a randomized position that is not any tile in which a wall
	 * is rendered.
	 * 
	 * @param icon The icon to put.
	 * @return The {@link RoomGraphic} created.
	 */
	public RoomGraphic createRandIcon(String icon) {
		return createIcon(((int) Math.random() * (height - 2)) + 1, (int) (Math.random() * (width - 2)) + 1, icon);
	}

	public Image createImage(int depth, int breadth, String[][] image) {
		var img = new Image(image, depth, breadth);
		graphics.add(img);
		return img;
	}

	public Image createRandIamge(String[][] image) {
		var md = image.length;
		var mw = 0;
		for (var s : image)
			if (s.length > mw)
				mw = s.length;
		return createImage((int) (Math.random() * (height - 1 - md) + 1), (int) (Math.random() * (width - 1 - mw) + 1),
				image);
	}

	/**
	 * Used for ASCII "images" being rendered onto the {@link RectangularDungeonRoom} that
	 * are rectangular, i.e. the provided <code>icon</code> parameter is a
	 * rectangular matrix (i.e. it is <em>not</em> jagged). This method is extremely
	 * similar to {@link #createRandIamge(String[][])}, but it can be used with
	 * rectangular icons (where every 1D array in the provided 2D array argument is
	 * of the same size) to speed up processing (because it takes the maximum width
	 * of the image to be the length of the first 1D array, rather than the max
	 * length of the 1D arrays).
	 * 
	 * @param image The rectangular image to render.
	 * @return The {@link Image} object built from the provided parameter.
	 */
	public Image createRandRectangularImage(String[][] image) {
		var md = image.length;
		var mw = image[0].length;
		return createImage((int) (Math.random() * (height - 1 - md) + 1), (int) (Math.random() * (width - 1 - mw) + 1),
				image);
	}

	public Set<RoomGraphic> getGraphics() {
		return graphics;
	}

	public Set<Opening> getOpenings() {
		return openings;
	}

	private void createHorizontalOpening(String[] arr, int width, int shift) {
		if ((width += 2) > arr.length - 2)
			width = arr.length - 2;
		else if (width < 3)
			width = 3;

		// Width is now the width of the gap + the two splitter characters.

		if (shift + width > arr.length - 2)
			shift = -(width - arr.length + 2);
		else if (shift < 0)
			shift = 0;

		arr[shift + 1] = SPLITTERS[3];
		Arrays.fill(arr, shift + 2, shift + width, " ");
		arr[shift + width] = SPLITTERS[2];
	}

	@Override
	public String[][] tilemap() {
		var map = new String[height][width];
		for (int i = 1; i < map.length - 1; i++) {
			map[i][0] = map[i][map[i].length - 1] = SIDE[0];
			Arrays.fill(map[i], 1, map[i].length - 1, " ");
		}
		Arrays.fill(map[0], 0, map[0].length - 1, SIDE[2]);
		Arrays.fill(map[map.length - 1], 0, map[0].length - 1, SIDE[2]);

		map[0][0] = CORNERS[1];
		map[0][width - 1] = CORNERS[0];
		map[height - 1][0] = CORNERS[3];
		map[height - 1][width - 1] = CORNERS[2];

		for (var opening : openings) {
			var gapwidth = opening.gapwidth;
			var pos = opening.position;
			if (opening.direction.isHorizontal())
				createHorizontalOpening(opening.direction == Direction.UP ? map[0] : map[map.length - 1], gapwidth,
						pos);
			else {
				int shift = opening.direction == Direction.RIGHT ? width - 1 : 0;
				if ((gapwidth += 2) > height - 2)
					gapwidth = height - 2;
				else if (gapwidth < 3)
					gapwidth = 3;

				if (pos + gapwidth > height - 2)
					pos -= pos + gapwidth - height + 2;
				else if (pos < 0)
					pos = 0;

				map[pos + 1][shift] = SPLITTERS[0];
				for (int i = 2; i < gapwidth; i++)
					map[pos + i][shift] = " ";
				map[pos + gapwidth][shift] = SPLITTERS[1];
			}
		}

		for (var v : graphics)
			v.render(map);

		return map;
	}

	/**
	 * Returns a {@link RectangularDungeonRoom} which renders as a square on Discord in a
	 * code block. (The width is a factor of <code>2.2</code> of the height, since
	 * the width of a character on Discord is less than the height in code blocks.)
	 * <code>width*2.2 = height</code>
	 * 
	 * @param size The height of the square.
	 * @return The new {@link RectangularDungeonRoom}.
	 */
	public static RectangularDungeonRoom discordSquare(int size) {
		return new RectangularDungeonRoom((int) (2.2 * size), size);
	}

}
