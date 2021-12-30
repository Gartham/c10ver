package gartham.c10ver.games.rpg.dungeons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gartham.c10ver.games.rpg.rooms.RectangularRoom.Graphic;

class RandLine implements Graphic {

	private final String[] elements;

	public RandLine(String[] elements, int position) {
		this(position, Arrays.copyOf(elements, elements.length));
	}

	public RandLine(String... elements) {
		this(-1, elements);
	}

	public RandLine(int position, String... elements) {
		this.elements = elements;
		this.position = position;
	}

	private final int position;

	@Override
	public void render(String[][] map) {
		int pos = position < 0 ? (int) (Math.random() * (map.length - 2) + 1) : position;
		Collections.shuffle(Arrays.asList(elements));// String is an OBJECT, not a primitive type (so this works :-).
		System.arraycopy(elements, 0, map[pos], (int) (Math.random() * (map[pos].length - elements.length - 1) + 1),
				elements.length);
	}

}

class RandLines implements Graphic {
	private final List<String[]> elements;

	public RandLines(List<String[]> elements) {
		this.elements = elements;
	}

	@Override
	public void render(String[][] map) {
		if (elements.size() > map.length - 2)
			throw new IllegalStateException(
					"There are too many random lines in this RandLines object to render to the specified tile map.");
		List<Integer> positions = new ArrayList<>();
		for (var rl : elements) {
			int pos = (int) (Math.random() * (map.length - 2 - positions.size()) + 1);
			var pp = Collections.binarySearch(positions, pos);
			if (pp < 0)
				pp = -pp;
			pos += pp;// TODO Verify.
			Collections.shuffle(Arrays.asList(elements));// String is an OBJECT, not a primitive type (so this works
															// :-).
			System.arraycopy(elements, 0, map[pos], (int) (Math.random() * (map[pos].length - rl.length - 1) + 1),
					rl.length);
			positions.add(-Collections.binarySearch(positions, pos) - 1, pos);
		}
	}
}