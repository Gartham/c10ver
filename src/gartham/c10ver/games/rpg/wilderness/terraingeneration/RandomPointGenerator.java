package gartham.c10ver.games.rpg.wilderness.terraingeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gartham.c10ver.games.rpg.wilderness.Location;

public class RandomPointGenerator {
	private final int sectionSize, density;

	private final Map<Location, Section> sections = new HashMap<>();
	private final Random rand = new Random();
	private final long seed = rand.nextLong();

	public RandomPointGenerator(int sectionSize, int density) {
		this.sectionSize = sectionSize;
		this.density = density;
	}

	public Section visitSection(int x, int y) {
		Location loc = Location.of(x, y);
		if (!sections.containsKey(loc))
			generateSection(x, y);
		return sections.get(loc);
	}

	/**
	 * Provides a point-density for the section at the specified <code>sectx</code>
	 * and <code>secty</code>. {@link RandomPointGenerator}'s default implementation
	 * simply returns the {@link RandomPointGenerator}'s {@link #density}.
	 * 
	 * @param sectx The x coordinate of the section in section coordinates ((0,0) is
	 *              the origin section).
	 * @param secty The y coordinate of the section in section coordinates.
	 * @return The density of terrain chunks at the specified section.
	 */
	protected int density(int sectx, int secty) {
		return density;
	}

	private void generateSection(int sectx, int secty) {
		Section s = new Section();
		rand.setSeed(seed ^ ((long) sectx << 32 | secty));
		for (int i = 0; i < density; i++)
			s.points.add(new Location(rand.nextInt(sectionSize), rand.nextInt(sectionSize)));
	}

	public class Section {
		private final List<Location> points = new ArrayList<>(1);

		private Section() {
		}
	}

}
