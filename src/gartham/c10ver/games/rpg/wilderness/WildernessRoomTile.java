package gartham.c10ver.games.rpg.wilderness;

import gartham.c10ver.games.rpg.rooms.StringRoom;
import gartham.c10ver.games.rpg.wilderness.WildernessMap.WildernessTile;

public class WildernessRoomTile extends WildernessMap<WildernessRoomTile>.WildernessTile implements StringRoom {

	private String backgroundTile;

	protected WildernessRoomTile(WildernessMap<WildernessRoomTile> enclosingMapInstance, int x, int y,
			String backgroundTile) {
		enclosingMapInstance.super(x, y);
		this.backgroundTile = backgroundTile;
	}

	@Override
	public String[][] tilemap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected WildernessRoomTile generateTile(LinkType link) {
		// TODO Auto-generated method stub
		return null;
	}

}
