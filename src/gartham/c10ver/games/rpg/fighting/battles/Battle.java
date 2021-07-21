package gartham.c10ver.games.rpg.fighting.battles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.fighting.Fighter;
import gartham.c10ver.games.rpg.fighting.Team;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class Battle {
	private final TextChannel channel;

	private final Map<Fighter, Integer> ticksTillTurn = new HashMap<>();
	private final List<Fighter> battleQueue = new ArrayList<>();
	private final List<Team> teams;

	public void start() {
		channel.sendMessage(new EmbedBuilder().setTitle(
				String.join(" vs ", JavaTools.mask(teams, t -> '`' + Utilities.stripBackticks(t.getName()) + '`')))
				.build()).queue();
	}

	private int getTTT(Fighter fighter) {
		return ticksTillTurn.get(fighter);
	}

	private void setTTT(Fighter fighter, int ticks) {
		ticksTillTurn.put(fighter, ticks);
	}

	public Battle(TextChannel channel, Team... teams) {
		this.channel = channel;
		this.teams = new ArrayList<>();
		for (var t : teams) {
			this.teams.add(t);
			for (var f : t)
				battleQueue.add(-Collections.binarySearch(battleQueue, f) - 1, f);
		}
	}

	public Battle(TextChannel channel, Collection<Team> teams) {
		this.channel = channel;
		this.teams = new ArrayList<>(teams);
	}

}
