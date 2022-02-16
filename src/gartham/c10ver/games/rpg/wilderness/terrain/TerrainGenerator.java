package gartham.c10ver.games.rpg.wilderness.terrain;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.games.rpg.wilderness.Location;

public abstract class TerrainGenerator<T> {
	private final int chunkWidth, chunkHeight;
	private final List<TerrainObjectGenerator<T>> generators = new ArrayList<>();

	public TerrainGenerator(int chunkWidth, int chunkHeight) {
		this.chunkWidth = chunkWidth;
		this.chunkHeight = chunkHeight;
	}

	public int getChunkHeight() {
		return chunkHeight;
	}

	public int getChunkWidth() {
		return chunkWidth;
	}
	
	public List<T> generateChunk(Location chunk) {
		List<T> items = new ArrayList<>();
		
		
		
		return items;
		
	}
	
}
