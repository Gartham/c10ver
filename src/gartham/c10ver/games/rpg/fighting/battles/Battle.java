package gartham.c10ver.games.rpg.fighting.battles;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.games.rpg.fighting.Fighter;
import net.dv8tion.jda.api.entities.TextChannel;

public class Battle {
	private final TextChannel channel;
	private final List<BattleEntity> battleQueue = new ArrayList<>();

	private final static class BattleEntity {
		private final Fighter fighter;
		private final int ticksTillTurn, health;

		public BattleEntity(Fighter fighter, int ticksTillTurn) {
			health = (this.fighter = fighter).health();
			this.ticksTillTurn = ticksTillTurn;
		}

		public Fighter getFighter() {
			return fighter;
		}

		public int getTicksTillTurn() {
			return ticksTillTurn;
		}
	}

	public Battle(TextChannel channel) {
		this.channel = channel;
	}

}
