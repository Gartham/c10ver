package gartham.c10ver.games.rpg.wilderness.terrain;

import java.util.Random;

import gartham.c10ver.games.rpg.RPGUtils;
import gartham.c10ver.games.rpg.random.Seed;
import gartham.c10ver.games.rpg.wilderness.Emoji;
import gartham.c10ver.games.rpg.wilderness.Location;
import gartham.c10ver.games.rpg.wilderness.terrain.noise.NoiseGenerator;
import gartham.c10ver.games.rpg.wilderness.terrain.noise.SmoothNoiseGenerator;

public class SmoothBiomeShader implements BiomeShader {

	private static final int SCALE_FACTOR = 5;

	private final NoiseGenerator ng = new SmoothNoiseGenerator(new Random().nextLong());

	private final Emoji[] emojis = Emoji.values();

//	public static void main(String[] args) {
//		for (int i = -10; i < 11; i++)
//			System.out.println("ORIGINAL: " + i + ", MAPPING: " + map(i));
//	}
//
//	public static String map(int loc) {
//		if (loc >= 0) {
//			return "(" + loc / SCALE_FACTOR + ", " + loc % SCALE_FACTOR + ")";
//		} else
//			return "(" + (loc - SCALE_FACTOR + 1) / SCALE_FACTOR + ", "
//					+ (SCALE_FACTOR - 1 - Math.abs(loc + 1) % SCALE_FACTOR) + ")";
//	}

	@Override
	public void shade(String[][] tile, Seed seed, Location tileLocation) {

		int rootTileX, rootTileY;
		int tileXIndex, tileYIndex;

		if (tileLocation.getX() >= 0) {
			rootTileX = tileLocation.getX() / SCALE_FACTOR;
			tileXIndex = tileLocation.getX() % SCALE_FACTOR;
		} else {
			rootTileX = (tileLocation.getX() - SCALE_FACTOR + 1) / SCALE_FACTOR;
			tileXIndex = SCALE_FACTOR - 1 - Math.abs(tileLocation.getX() + 1) % SCALE_FACTOR;
		}

		if (tileLocation.getY() >= 0) {
			rootTileY = -tileLocation.getY() / SCALE_FACTOR;
			tileYIndex = SCALE_FACTOR - 1 - tileLocation.getY() % SCALE_FACTOR;
		} else {
			rootTileY = -(tileLocation.getY() - SCALE_FACTOR + 1) / SCALE_FACTOR;
			tileYIndex = Math.abs(tileLocation.getY() + 1) % SCALE_FACTOR;
		}

		Location newTileLocation = Location.of(rootTileX, rootTileY);
//		System.out.println("OLD: " + tileLocation + ", NEW: ([" + rootTileX + ", " + tileXIndex + "], [" + rootTileY
//				+ ", " + tileYIndex + "])");

		var sm = ng.noisemap(seed.pick(RPGUtils.CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_SOFTMAP), newTileLocation,
				tileXIndex * tile[0].length, tileYIndex * tile.length, (tileXIndex + 1) * tile[0].length,
				(tileYIndex + 1) * tile.length, tile[0].length * SCALE_FACTOR, tile.length * SCALE_FACTOR);
		var hm = ng.noisemap(seed.pick(RPGUtils.CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_HARDMAP), newTileLocation,
				tileXIndex * tile.length, tileYIndex * tile[0].length, (tileXIndex + 1) * tile.length,
				(tileYIndex + 1) * tile[0].length, tile.length * SCALE_FACTOR, tile[0].length * SCALE_FACTOR);

//		var sm = ng.noisemap(seed.pick(RPGUtils.CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_SOFTMAP), tileLocation, 0, 0,
//				tile.length, tile.length, tile.length, tile.length);
//		var hm = ng.noisemap(seed.pick(RPGUtils.CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_HARDMAP), tileLocation, 0, 0,
//				tile.length, tile.length, tile.length, tile.length);

//		var map = ng.noisemap(seed.pick(RPGUtils.CLOVER_WILDERNESS_RANDOM_SEED_TERRAIN_SOFTMAP),
//			TILE=tileLocation / 5, 
//			TILE=tileLocation / 5, 
//			(INDEX=tileLocation % 5) * tile.length, 
//			(INDEX=tileLocation % 5) * tile.length,
//			TOTAL_SIZE=(tile.length*GRANULARITY), 
//			TOTAL_SIZE=(tile.length*GRANULARITY));

//		for (var x : sm) {
//			System.out.print("[");
//			for (var y : x)
//				System.out.print((Math.round(y * 100)) / 100d + ", ");
//			System.out.println("]");
//		}

		for (int i = 0; i < sm.length; i++)
			for (int j = 0; j < sm[0].length; j++)
				tile[j][i] = pickBiome(i, j, sm[i][j], hm[i][j]).getValue();
//				tile[j][i] = emojis[((int) ((sm[i][j] / 2 + .5) * emojis.length)) % emojis.length].getValue();

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
