package gartham.c10ver.games.rpg.wilderness.terrain;

import gartham.c10ver.games.rpg.wilderness.Location;

public class WallPointBiomeShader implements BiomeShader {

	private PointGenerator generator;

	public WallPointBiomeShader(PointGenerator generator) {
		this.generator = generator;
	}

	public PointGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(PointGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void shade(String[][] tile, Seed seed, Location tileLocation) {
		// TODO Auto-generated method stub

	}

}
