package gartham.c10ver.games.rpg.fighting.battles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gartham.c10ver.games.rpg.fighting.Fighter;
import net.dv8tion.jda.api.entities.TextChannel;

public class Battle {
	private final TextChannel channel;

	private final Map<Fighter, Integer> ticksTillTurn = new HashMap<>();
	private final List<Fighter> battleQueue = new ArrayList<>();

	private int getTTT(Fighter fighter) {
		return ticksTillTurn.get(fighter);
	}

	private void setTTT(Fighter fighter, int ticks) {
		ticksTillTurn.put(fighter, ticks);
	}

	public Battle(TextChannel channel) {
		this.channel = channel;
	}

}
