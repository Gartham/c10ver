package gartham.c10ver.games.rpg.dungeons;

import java.util.Arrays;
import java.util.Collections;

import gartham.c10ver.games.rpg.rooms.RectangularRoom.Graphic;

class RandLine implements Graphic {

	private final String[] elements;

	public RandLine(String[] elements, int position) {
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
