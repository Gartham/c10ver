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

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * <p>
 * A {@link Battle} is a fight between at least two {@link Team}s, each composed
 * of at least one {@link Fighter}. {@link Battle}s are <b>turn-based</b>, with
 * each turn being taken through the doing of an <b>action</b> by a
 * {@link Fighter}. Each action "takes" a certain number of "ticks", which are,
 * in a sense, time in a battle. When a fighter acts during its turn, the number
 * of ticks the action takes is the number of ticks "until" it is that same
 * {@link Fighter}'s turn to make its next move. Upon the conclusion of any
 * turn, if all {@link Fighter}s on a {@link Team} are considered dead (i.e.
 * their health is <code>0</code>), that {@link Team} is considered dead. Once
 * all but one {@link Team} is considered dead, that {@link Team} wins the
 * {@link Battle}. If, upon the conclusion of a turn, all {@link Team}s are
 * dead, the battle is a considered to have been won by the {@link Team} whose
 * {@link Fighter} had the last turn.
 * </p>
 * <p>
 * Each turn, a {@link Fighter} takes an <b>action</b>. Actions take a certain
 * number of ticks and can modify the {@link Battle} in any way that the
 * {@link Battle} permits. Subclasses may expose behavior and functionality that
 * modifies the battle as they see fit. They may also expose behavior and
 * functionality that build upon the basic concept of a {@link Battle} (e.g.
 * adding "effects" that target {@link Fighter}s, which are kept track of by the
 * {@link Battle} and cease their effects after a certain number of ticks,
 * affecting the targeted {@link Fighter} while extant). {@link Battle}
 * subclasses should expose the actions that can be taken by a {@link Fighter}
 * to calling code through some means.
 * </p>
 * 
 * @author Gartham
 *
 * @param <A> The type representing the actions that can be taken by
 *            {@link Fighter}s tracked by this {@link Battle}.
 */
public abstract class Battle<A> {

	private final Map<Fighter, Integer> ticksTillTurn = new HashMap<>();
	private final List<Fighter> battleQueue = new ArrayList<>();
	private final List<Team> teams;

	/**
	 * Starts this {@link Battle} by
	 */
	public void start() {
		// Assign initial ticks.
		var max = battleQueue.get(0).getSpeed();

		for (Fighter f : battleQueue)
			setTTT(f, new BigDecimal(max.subtract(f.getSpeed())).multiply(BigDecimal.valueOf(Math.random() / 5 + 0.9))
					.intValue());

		Collections.sort(battleQueue, (o1, o2) -> Integer.compare(getTTT(o1), getTTT(o2)));

//		channel.sendMessage(printBattleQueue()).queue();
	}

	private String getField(Fighter f) {
		return "\\\u2764\uFE0F `" + f.getHealth() + "/" + f.getMaxHealth() + "`   \\\u2694\uFE0F `" + f.getAttack()
				+ "`   \\\uD83D\uDEE1\uFE0F \u200b `" + f.getDefense() + "`   \\\uD83D\uDCA8\uFE0F `" + f.getSpeed()
				+ "`\n\uD83D\uDD50\uFE0F **" + getTTT(f) + "**\nTeam: " + f.getTeam().getName();
	}

	private MessageEmbed printBattleQueue() {
		EmbedBuilder builder = new EmbedBuilder().setTitle(String.join(" vs ", JavaTools.mask(teams, Team::getName)));
		if (!battleQueue.isEmpty()) {
			for (int i = 0; i < battleQueue.size() - 1; i++) {
				var f = battleQueue.get(i);
				builder.addField(f.getEmoji() + ' ' + f.getName(), getField(f) + "\n\u200b", false);
			}
			var f = battleQueue.get(battleQueue.size() - 1);
			builder.addField(f.getEmoji() + ' ' + f.getName(), getField(f), false);
		}
		return builder.build();
	}

	/**
	 * Acts as the current
	 * 
	 * @param action
	 */
	public abstract void act(A action);

	public void sortQueue() {
		Collections.sort(battleQueue, (o1, o2) -> Integer.compare(getTTT(o1), getTTT(o2)));
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

	public Battle(Team... teams) {
		this.teams = new ArrayList<>();
		for (var t : teams) {
			this.teams.add(t);
			for (var f : t)
				battleQueue.add(
						-Collections.binarySearch(battleQueue, f, Comparator.<Fighter>naturalOrder().reversed()) - 1,
						f);
		}
	}

	public Battle(Collection<Team> teams) {
		this.teams = new ArrayList<>(teams);

	}

}
