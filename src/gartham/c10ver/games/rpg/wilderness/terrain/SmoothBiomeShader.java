package gartham.c10ver.games.rpg.wilderness.terrain;

import java.util.Random;

import gartham.c10ver.games.rpg.RPGUtils;
import gartham.c10ver.games.rpg.random.Seed;
import gartham.c10ver.games.rpg.wilderness.Emoji;
import gartham.c10ver.games.rpg.wilderness.Location;
import gartham.c10ver.games.rpg.wilderness.terrain.noise.NoiseGenerator;
import gartham.c10ver.games.rpg.wilderness.terrain.noise.SmoothNoiseGenerator;

public class SmoothBiomeShader implements BiomeShader {

	private final NoiseGenerator ng = new SmoothNoiseGenerator(new Random().nextLong());

	private final Emoji[] emojis = Emoji.values();

	@Override
	public void shade(String[][] tile, Seed seed, Location tileLocation) {

//		int rootTileX = Math.abs(tileLocation.getX() / GRANULARITY), rootTileY = Math.abs(tileLocation.getY() / GRANULARITY);
//		int tileXIndex = Math.abs(tileLocation.getX()) % GRANULARITY, tileYIndex = Math.abs(tileLocation.getY()) % GRANULARITY;
//
//		var nm = ng.noisemap(Location.of(rootTileX, rootTileY), tileXIndex * tile.length, tileYIndex * tile[0].length,
//				(tileXIndex + 1) * tile.length, (tileYIndex + 1) * tile[0].length, tile.length * GRANULARITY,
//				tile[0].length * GRANULARITY);

		var sm = ng.noisemap(seed.pick(RPGUtils.CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_SOFTMAP),
				Location.of(tileLocation.getX(), -tileLocation.getY()), 0, 0, tile.length, tile.length, tile.length,
				tile.length);
		var hm = ng.noisemap(seed.pick(RPGUtils.CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_HARDMAP),
				Location.of(tileLocation.getX(), -tileLocation.getY()), 0, 0, tile.length, tile.length, tile.length,
				tile.length);

		for (int i = 0; i < sm.length; i++)
			for (int j = 0; j < sm[0].length; j++)
				tile[j][i] = pickBiome(i, j, sm[i][j], hm[i][j]).getValue();

	}

	public static Emoji pickBiome(int x, int y, double sv, double hv) {
		sv = sv / 2 + 0.5;
		hv = hv / 2 + .5;
		if (sv <= 0.3 && hv <= 0.2)
			return Emoji.BLACK;
		else if (sv <= 0.6 && hv <= 0.4)
			return Emoji.ORANGE;
		else if (sv <= 0.7 && hv <= 0.3)
			return Emoji.PURPLE;
		else if (sv <= 0.5 && hv <= 0.8)
			return Emoji.GREEN;
		else if (sv <= 0.8)
			return Emoji.RED;
		else
			return Emoji.YELLOW;
	}

}
