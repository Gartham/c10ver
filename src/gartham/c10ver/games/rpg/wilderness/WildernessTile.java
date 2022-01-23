package gartham.c10ver.games.rpg.wilderness;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WildernessTile {
	private final Map<LinkType, WildernessTile> linkedTiles = new HashMap<>(2);

	public Map<LinkType, WildernessTile> getLinkedTiles() {
		return Collections.unmodifiableMap(linkedTiles);
	}

	void link(WildernessTile other, LinkType link) {
		linkedTiles.put(link, other);
	}

	public WildernessTile get(LinkType link) {
		return linkedTiles.get(link);
	}

	public WildernessTile() {
	}

}
