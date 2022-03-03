package gartham.c10ver.games.rpg.wilderness.terrain;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import gartham.c10ver.games.rpg.wilderness.Location;

interface GradGenerator {
	Vec generate(int x, int y);

	static GradGenerator continuous(long seed) {
		Random r = new Random(seed);
		Map<Location, Vec> vectors = new HashMap<>();
		return (x, y) -> {
			if (vectors.containsKey(Location.of(x, y)))
				return vectors.get(Location.of(x, y));
			else {
				double sqr = r.nextDouble() * 2;
				Vec vec = new Vec((r.nextBoolean() ? -1 : 1) * Math.sqrt(sqr),
						(r.nextBoolean() ? -1 : 1) * Math.sqrt(2 - sqr));
				vectors.put(Location.of(x, y), vec);
				return vec;
			}
		};
	}

	Vec TL = new Vec(-1, 1), TR = new Vec(1, 1), BR = new Vec(1, -1), BL = new Vec(-1, -1);

	static GradGenerator discrete(long seed) {
		Random r = new Random(seed);
		return (x, y) -> {
			return r.nextBoolean() ? r.nextBoolean() ? TL : TR : r.nextBoolean() ? BR : BL;
		};
	}

}