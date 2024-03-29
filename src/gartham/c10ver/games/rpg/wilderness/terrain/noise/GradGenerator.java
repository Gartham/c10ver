package gartham.c10ver.games.rpg.wilderness.terrain.noise;

import java.util.Random;

import gartham.c10ver.games.rpg.random.Seed;
import gartham.c10ver.games.rpg.wilderness.Location;

/**
 * A stateless <i>expression</i> that maps from a location and seed to a new
 * {@link Vec}. {@link GradGenerator}s deterministically map every valid
 * {@link Location} to a pseudorandomly generated {@link Vec}, with
 * pseudorandomness manipulable through choice of seed. This type is used by
 * {@link SmoothNoiseGenerator}s to generate perlin noise.
 * 
 * @author Gartham
 *
 */
interface GradGenerator {
	Vec generate(Location location, Seed seed);

	static GradGenerator continuous() {
		return (loc, seed) -> {

			// Hope the randomness isn't too crap.
			Random r = seed.pick(loc.getX(), loc.getY()).rand();
			double sqr = r.nextDouble() * 2;
			Vec vec = new Vec((r.nextBoolean() ? -1 : 1) * Math.sqrt(sqr),
					(r.nextBoolean() ? -1 : 1) * Math.sqrt(2 - sqr));
			return vec;
		};
	}

	Vec TL = new Vec(-1, 1), TR = new Vec(1, 1), BR = new Vec(1, -1), BL = new Vec(-1, -1);

	static GradGenerator discrete() {
		return (loc, sed) -> {
			var r = sed.pick(loc.getX(), loc.getY()).rand();
			return r.nextBoolean() ? r.nextBoolean() ? TL : TR : r.nextBoolean() ? BR : BL;
		};
	}

}