package gartham.c10ver.games.rpg.wilderness.terrain;

import java.util.Random;

import gartham.c10ver.games.rpg.wilderness.Emoji;
import gartham.c10ver.games.rpg.wilderness.Location;

public class SmoothBiomeShader implements BiomeShader {

	private final Emoji[] emojis = Emoji.values();

	@Override
	public void shade(String[][] tile, Seed seed, Location tileLocation) {
		Random r = new Random(seed.getSeed());

		for (String[] s : tile)
			for (int i = 0; i < s.length; i++)
				s[i] = emojis[r.nextInt(emojis.length)].getValue();
	}

}
