package gartham.c10ver.games.rpg.wilderness.biomes;

import java.util.Random;

import gartham.c10ver.games.rpg.RPGUtils;
import gartham.c10ver.games.rpg.random.Seed;
import gartham.c10ver.games.rpg.wilderness.Emoji;
import gartham.c10ver.games.rpg.wilderness.Location;

public interface Biome {
	String color(Seed seed, Location tile, int xpix, int ypix);

	interface DeterministicBiome extends Biome {
		String color(Location tile, int xpix, int ypix);

		@Override
		default String color(Seed seed, Location tile, int xpix, int ypix) {
			return color(tile, xpix, ypix);
		}
	}

	DeterministicBiome DESERT = (tile, xpix, ypix) -> Emoji.ORANGE.getValue();
	DeterministicBiome GRASSLAND = (tile, xpix, ypix) -> Emoji.GREEN.getValue();
	Biome BURNING_GRASSLANDS = new Biome() {
		@Override
		public String color(Seed seed, Location tile, int xpix, int ypix) {
			Random r = seed.pick(RPGUtils.BURNING_GRASSLANDS_FIRE).rand();
			return ((xpix + ypix) % 3 == 1 && r.nextDouble() > 0.5 ? Emoji.RED : Emoji.GREEN).getValue();
		}
	};
}
