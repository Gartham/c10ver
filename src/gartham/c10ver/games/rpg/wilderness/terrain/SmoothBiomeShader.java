package gartham.c10ver.games.rpg.wilderness.terrain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.wilderness.Emoji;
import gartham.c10ver.games.rpg.wilderness.Location;
import gartham.c10ver.games.rpg.wilderness.terrain.noise.NoiseGenerator;
import gartham.c10ver.games.rpg.wilderness.terrain.noise.SmoothNoiseGenerator;

public class SmoothBiomeShader implements BiomeShader {

	// TEMPORARY WORKAROUND; PROVIDE RANDOM SEED AND IGNORE WORLD GEN SEED WHEN
	// USING SMOOTH_NOISE_GENERATOR.
	private final NoiseGenerator ng = new SmoothNoiseGenerator(new Random().nextLong());

//	private static final int GRANULARITY = 5;

	private static final MessageDigest md;

	static {
		MessageDigest md2;
		try {
			md2 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			md2 = null;
			System.err.println("No available MD5 hashing algorithm for Smooth Biome Shader!");
		}

		md = md2;
	}

	private final Emoji[] emojis = Emoji.values();

	private static long calculateLocalSeed(Seed seed, Location location, int index) {

		int x = location.getX() / 2, y = location.getY() / 2;

		byte[] msg = new byte[20];
		long s = seed.getSeed();
		for (byte i = 0; i < 8; i++, s >>= 8)
			msg[i] = (byte) (s & 0xff);

		s = (long) x << 32 | y;
		for (byte i = 0; i < 8; i++, s >>= 8)
			msg[i + 8] = (byte) (s & 0xff);

		for (byte i = 0; i < 4; i++, index >>= 8)
			msg[i + 4] = (byte) (index & 0xff);

		return JavaTools.bytesToLong(md.digest(msg));
	}

	@Override
	public void shade(String[][] tile, Seed seed, Location tileLocation) {

//		int rootTileX = Math.abs(tileLocation.getX() / GRANULARITY), rootTileY = Math.abs(tileLocation.getY() / GRANULARITY);
//		int tileXIndex = Math.abs(tileLocation.getX()) % GRANULARITY, tileYIndex = Math.abs(tileLocation.getY()) % GRANULARITY;
//
//		var nm = ng.noisemap(Location.of(rootTileX, rootTileY), tileXIndex * tile.length, tileYIndex * tile[0].length,
//				(tileXIndex + 1) * tile.length, (tileYIndex + 1) * tile[0].length, tile.length * GRANULARITY,
//				tile[0].length * GRANULARITY);

		var nm = ng.noisemap(Location.of(tileLocation.getX(), -tileLocation.getY()), 0, 0, tile.length, tile.length,
				tile.length, tile.length);

		for (int i = 0; i < nm.length; i++)
			for (int j = 0; j < nm[0].length; j++)
				tile[j][i] = emojis[(int) ((nm[i][j] / 2 + .5) * emojis.length)].getValue();

	}

}
