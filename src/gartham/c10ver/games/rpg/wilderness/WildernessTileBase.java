package gartham.c10ver.games.rpg.wilderness;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.rooms.RoomGraphic;
import gartham.c10ver.games.rpg.rooms.StringRoom;

public class WildernessTileBase extends WildernessMap<WildernessTileBase>.WildernessTile implements StringRoom {

	public static final String DEFAULT_BACKGROUND_STR = "\u2B1B";
	public static final int DEFAULT_BACKGROUND_WIDTH = 24, DEFAULT_BACKGROUND_HEIGHT = 30;

	private String backgroundPixel;
	private final int width, height;

	private final Set<RoomGraphic> graphix = new HashSet<>();

	public String getBackgroundPixel() {
		return backgroundPixel;
	}

	public void setBackgroundTile(String backgroundTile) {
		this.backgroundPixel = backgroundTile;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public WildernessTileBase(WildernessMap<WildernessTileBase> enclosingMapInstance, int x, int y,
			String backgroundPixel) {
		this(enclosingMapInstance, x, y, backgroundPixel, DEFAULT_BACKGROUND_WIDTH, DEFAULT_BACKGROUND_HEIGHT);
	}

	public WildernessTileBase(WildernessMap<WildernessTileBase> enclosingMapInstance, int x, int y,
			String backgroundPixel, int width, int height) {
		enclosingMapInstance.super(x, y);
		this.backgroundPixel = backgroundPixel;
		this.width = width;
		this.height = height;
	}

	public WildernessTileBase(WildernessMap<WildernessTileBase> enclosingMapInstance, int x, int y) {
		this(enclosingMapInstance, x, y, DEFAULT_BACKGROUND_STR);
	}

	public Set<RoomGraphic> getGraphix() {
		return graphix;
	}

	@Override
	public String[][] tilemap() {
		String[][] map = new String[height][width];
		JavaTools.fill(map, backgroundPixel);
		for (var rg : graphix)
			rg.render(map);
		return map;
	}

	@Override
	protected WildernessTileBase generateTile(LinkType link) {
		// TODO Auto-generated method stub
		return null;
	}

}
