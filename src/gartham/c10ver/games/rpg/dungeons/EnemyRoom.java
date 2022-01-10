package gartham.c10ver.games.rpg.dungeons;

import java.util.ArrayList;

import gartham.c10ver.games.rpg.fighting.battles.app.GarmonTeam;
import gartham.c10ver.games.rpg.rooms.RectangularDungeonRoom;

public class EnemyRoom extends DungeonRoom {

	private final RandLines lines;
	private final GarmonTeam enemies;

	public GarmonTeam getEnemies() {
		return enemies;
	}

	public EnemyRoom(GarmonTeam enemies) {
		super(RectangularDungeonRoom.discordSquare((int) (Math.random() * 6 + 14)));
		this.enemies = enemies;
		ArrayList<String[]> l = new ArrayList<>();
		lines = new RandLines(l);
		l.add(new String[] { "\uD83D\uDC3A", "", "", "", "", "\u3000", "\u3000", "\u3000" });
		if (enemies.memberView().size() > 2) {
			l.add(new String[] { "\uD83D\uDC3A", "", "", "", "", "\u3000", "\u3000", "\u3000" });
			if (enemies.memberView().size() > 4) {
				l.add(new String[] { "\uD83D\uDC3A", "", "", "", "", "\u3000", "\u3000", "\u3000" });
				if (enemies.memberView().size() > 7) {
					l.add(new String[] { "\uD83D\uDC3A", "", "", "", "", "\u3000", "\u3000", "\u3000" });
					if (enemies.memberView().size() > 10)
						l.add(new String[] { "\uD83D\uDC3A", "", "", "", "", "\u3000", "\u3000", "\u3000" });
				}
			}
		}

		getRoom().getGraphics().add(lines);
	}

}
