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
import gartham.c10ver.games.rpg.fighting.FighterController;
import gartham.c10ver.games.rpg.fighting.Team;
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

	private String getField(Fighter f) {
		return "\\\u2764\uFE0F `" + f.getHealth() + "/" + f.getMaxHealth() + "`   \\\u2694\uFE0F `" + f.getAttack()
				+ "`   \\\uD83D\uDEE1\uFE0F \u200b `" + f.getDefense() + "`   \\\uD83D\uDCA8\uFE0F `" + f.getSpeed()
				+ "`\n\uD83D\uDD50\uFE0F **" + getTTT(f) + "**\nTeam: " + f.getTeam().getName();
	}

	public void start() {
		// Assign initial ticks.
		var max = battleQueue.get(0).getSpeed();

		for (Fighter f : battleQueue)
			setTTT(f, new BigDecimal(max.subtract(f.getSpeed())).multiply(BigDecimal.valueOf(Math.random() / 5 + 0.9))
					.intValue());

		Collections.sort(battleQueue, (o1, o2) -> Integer.compare(getTTT(o1), getTTT(o2)));

		EmbedBuilder builder = new EmbedBuilder().setTitle(String.join(" vs ", JavaTools.mask(teams, Team::getName)));
		if (!battleQueue.isEmpty()) {
			for (int i = 0; i < battleQueue.size() - 1; i++) {
				var f = battleQueue.get(i);
				builder.addField(f.getEmoji() + ' ' + f.getName(), getField(f) + "\n\u200b", false);
			}
			var f = battleQueue.get(battleQueue.size() - 1);
			builder.addField(f.getEmoji() + ' ' + f.getName(), getField(f), false);

		}
		channel.sendMessage(builder.build()).queue();

	}

	/**
	 * Continues this {@link Battle} by invoking the next {@link Fighter} in the
	 * queue. This is normally called by a {@link FighterController} after it has
	 * finished acting.
	 */
	public void nextTurn() {
		Collections.sort(battleQueue, (o1, o2) -> Integer.compare(getTTT(o1), getTTT(o2)));
		battleQueue.get(0).getController().act();
	}

	private int getTTT(Fighter fighter) {
		return ticksTillTurn.get(fighter);
	}

	private void setTTT(Fighter fighter, int ticks) {
		ticksTillTurn.put(fighter, ticks);
	}

	public void addTicks(Fighter fighter, int ticks) {
		setTTT(fighter, getTTT(fighter) + ticks);
	}

	public void setTicks(Fighter fighter, int ticks) {
		setTicks(fighter, ticks);
	}

	public int getTicks(Fighter fighter, int ticks) {
		return getTTT(fighter);
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
