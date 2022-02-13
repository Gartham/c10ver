package gartham.c10ver.games.rpg.wilderness.terraingeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gartham.c10ver.games.rpg.wilderness.Location;

public class RandomPointGenerator {
	private final int sectionSize, minPointDistance;

	private final Map<Location, Section> sections = new HashMap<>();

	public RandomPointGenerator(int sectionSize, int minPointDistance) {
		this.sectionSize = sectionSize;
		this.minPointDistance = minPointDistance;
	}

	public void visitSection(int x, int y) {
		if (!sections.containsKey(Location.of(x, y))) {
			generateSection(x, y);
		}
	}

	private void generateSection(int x, int y) {
		
	}

	public class Section {
		private final List<Location> points = new ArrayList<>(1);
	}

}
