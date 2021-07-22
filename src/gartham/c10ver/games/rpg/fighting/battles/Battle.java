package gartham.c10ver.games.rpg.fighting.battles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

	private void debug(String msg) {
		channel.sendMessage("[DEBUG]: " + msg).queue();
	}

	public void start() {

		debug("Speeds: ["
				+ String.join(", ", JavaTools.mask(battleQueue, a -> a.getName() + "(`" + a.getSpeed() + "`)")) + ']');

		// Assign initial ticks.
		var max = battleQueue.get(0).getSpeed();

		for (Fighter f : battleQueue)
			setTTT(f, new BigDecimal(max.subtract(f.getSpeed())).multiply(BigDecimal.valueOf(Math.random() / 5 + 0.9))
					.intValue());

		Collections.sort(battleQueue, (o1, o2) -> Integer.compare(getTTT(o1), getTTT(o2)));

		EmbedBuilder builder = new EmbedBuilder().setTitle(
				String.join(" vs ", JavaTools.mask(teams, t -> '`' + Utilities.stripBackticks(t.getName()) + '`')));
		for (var f : battleQueue)
			builder.addField(f.getEmoji() + ' ' + f.getTeam().getName(), "\uD83D\uDD50\uFE0F " + getTTT(f), false);
		channel.sendMessage(builder.build()).queue();
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
				battleQueue.add(
						-Collections.binarySearch(battleQueue, f, Comparator.<Fighter>naturalOrder().reversed()) - 1,
						f);
		}
	}

	public Battle(TextChannel channel, Collection<Team> teams) {
		this.channel = channel;
		this.teams = new ArrayList<>(teams);
	}

}
