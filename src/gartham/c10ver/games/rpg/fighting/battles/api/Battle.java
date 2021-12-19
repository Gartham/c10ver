package gartham.c10ver.games.rpg.fighting.battles.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

/**
 * <h1>Concept</h1><i>- A brief overview of the <b>concept</b> of battles.</i>
 * <p>
 * This class models a <i>battle</i>, which is a structured conflict that occurs
 * between two (or more) parties, each known as a <i>team</i>. Battles are
 * carried out by <i>fighters</i>, (each of which belong to a team), that
 * <i>make moves</i> with the goal of having their team <i>win</i> the battle.
 * </p>
 * <p>
 * At any given moment in a battle, it is a <b>single</b> fighter's <i>turn</i>
 * to make a move, and each fighter works with the goal of eliminating all other
 * fighters of all other teams, from the battle. A fighter is considered
 * "eliminated" when its health is <code>0</code> or less, and a team is
 * considered eliminated when all of its fighters are eliminated.
 * </p>
 * <p>
 * Once all but one team is/are eliminated, the one remaining team is considered
 * the winner of the battle.
 * </p>
 * <h2>Moves</h2>
 * <p>
 * When it is one's turn, a fighter must make a move to affect the state of the
 * battle. This class considers a move to be any action that:
 * <ol>
 * <li><i>possibly</i> affects the state of a battle and,</li>
 * <li>takes a non-negative amount of <i>ticks</i>.</li>
 * </ol>
 * The set of moves that a fighter can make and the behavior of those moves with
 * regard to the battle is determined by the developer utilizing this class
 * </p>
 * <h2>Ticks</h2>
 * <p>
 * In a battle, each fighter must wait a certain number of <i>ticks</i> before
 * it can make its next move. The number of ticks a fighter must wait before
 * making a move is referred to as that fighter's ticks (possessively). <i>A
 * fighter's ticks is the number of ticks that fighter must wait before making
 * its next move.</i>
 * </p>
 * <p>
 * <ul>
 * <li>When it is a fighter's turn to make a move, that fighter has
 * <code>0</code> ticks by definition (because it must wait <code>0</code> ticks
 * before making its move).</li>
 * <li>Any move that a fighter can make <i>costs</i> a certain number of ticks.
 * This cost is added to the fighter's ticks when the fighter makes a move.</li>
 * <li>When a fighter concludes making a move, the following two operations are
 * performed, in order:
 * <ol>
 * <li>The cost of that move is added to that fighter's ticks.</li>
 * <li>The ticks of the fighter with the lowest number of ticks is subtracted
 * from <i>all</i> fighters. This is the battle analogue of "time passing."</li>
 * </ol>
 * </li>
 * </ul>
 * The cost of a move can be imagined as the number of ticks before the fighter
 * will get to act again. When a fighter makes its move, it must wait before
 * making a second move, so the first move's cost is added to its ticks.
 * Correspondingly, it must then be a (different) fighter's turn to make a move,
 * so the battle "waits through" the number of ticks of the fighter with the
 * lowest number of ticks. This fighter then gets a tick count of
 * <code>0</code>, and then gets to move.
 * </p>
 * <figure><img style="display:block;" height=200px src=
 * "doc-files/TickingExample.png"><figcaption>Example of a battle modeled as a
 * state transition diagram. Each rectangle (named "Battle" in the top left) is
 * a state, and transitions are denoted with colored arrows.
 * <span style="color:#B85450;">Red arrows</span> denote moves made by a fighter
 * represented by a <span style="color:#B85450;">red box</span>, and
 * respectively for the <span style="color:#82B366;">green arrows</span>. Ticks
 * are denoted with <span style="color:#D6B656;">gold colored</span> boxes
 * inside fighters.<br>
 * Initially, it is <span style="color:#B85450;">Red's</span> turn, and
 * <span style="color:#82B366;">Green</span> has <code>30</code> ticks.
 * <span style="color:#B85450;">Red</span> then makes a move that costs
 * <code>500</code> ticks, which causes <span style="color:#B85450;">Red</span>
 * to have <code>500</code> ticks and <span style="color:#82B366;">Green</span>
 * to have <code>30</code>. The battle then calculates the next fighter's turn
 * (<span style="color:#82B366;">Green</span>, since
 * <span style="color:#82B366;">Green</span> has the fewest ticks of the two
 * fighters) and reduces all fighters ticks by that amount, making
 * <span style="color:#82B366;">Green</span>'s ticks <code>0</code> and leaving
 * <span style="color:#B85450;">Red</span> with <code>470</code>.
 * <span style="color:#B85450;">Red</span> must then wait until its tick count
 * reaches <code>0</code> again to make its next move.</figcaption></figure>
 * <p>
 * Battle ticks are an analogue of real time (they can be imagined as "battle
 * time") but should not be confused with real time; the two are completely
 * disjoint. Operations "costing" ticks can be performed instantaneously with
 * respect to real time, which is why battles are referred to as "asynchronous."
 * No operation in this class is specified to be a blocking operation per this
 * documentation.
 * </p>
 * <h2>State System</h2>
 * <p>
 * Battles can be modeled as a state system that changes state only when a move
 * is made by a fighter.<figure><img style="display:block;" width=400px src=
 * "doc-files/BattleState.png"/><figcaption>Diagram detailing the state of a
 * battle. An arrow denotes <i>containment</i>, in the sense that the state of a
 * Battle contains the state of multiple teams.</figcaption></figure> A battle's
 * state is made up of the states of that battle's participating teams, and the
 * queue of fighters. Each team's state comprises the state of each of that
 * team's fighters, (including the fighter's health and status effects
 * (buffs/debuffs)). This gives a hierarchy as depicted in the diagram.
 * </p>
 * <p>
 * Battles change state whenever a fighter makes a move. Making a move can
 * affect the position of each fighter in the fighter queue (as denoted below),
 * the state of each fighter, and the state of each team.
 * </p>
 * <h1>Interface</h1>
 * <h2>Usage</h2>
 * <p>
 * Constructing a {@link Battle}
 * </p>
 * <p>
 * After construction, the {@link Battle} is in a valid state. Typical calling
 * code will perform the following, in order:
 * <ol>
 * <li>Get the {@link #getCurrentFighter() current fighter} ({@link Fighter}
 * whose turn it is).</li>
 * <li>Carry out that {@link Fighter}'s move (by affecting the state of other
 * {@link Fighter}s and of {@link Team}s, e.g. the {@link #getCurrentFighter()
 * current fighter} may attack another {@link Fighter} by lowering the other
 * {@link Fighter}'s health).</li>
 * <li>Call {@link #act(int)}, providing the number of ticks the
 * {@link #getCurrentFighter() current fighter}'s move took</li>
 * </ol>
 * and repeat these steps until the battle is completed (i.e.
 * {@link #getWinningTeam()} does not return <code>null</code>).
 * </p>
 * <p>
 * Calling {@link #act(int)} rectifies the state of the {@link Battle} so that:
 * <ol>
 * <li>The elimination (or revival) of any {@link Fighter}s (since the last
 * {@link Fighter}'s move) is reflected by the {@link Battle} (this adds/removes
 * members of the {@link #getBattleQueue() battle queue} and
 * {@link #getRemainingTeams() remaining teams} lists when needed).</li>
 * <li>The number of ticks that the action taken by {@link #getCurrentFighter()}
 * took is added to that {@link Fighter}'s ticks.</li>
 * <li>The {@link #getBattleQueue() battle queue} is sorted and normalized (it
 * is ordered ascendingly and so that the first {@link Fighter} in it, (i.e.,
 * the {@link Fighter} at position <code>0</code>), has <code>0</code>
 * ticks).</li>
 * </ol>
 * Each of these rectifications are performed in order to ensure state
 * consistency.
 * </p>
 * 
 * <h1>Implementation</h1>
 * <p>
 * This implementation is hardy: If the
 * 
 * @author Gartham
 *
 */
public class Battle<F extends Fighter, T extends Team<F>> {

	private F currentFighter;// This is kept track of between moves so that if the current fighter dies, or
								// otherwise, and #updateFighterStates() removes it from the battle queue,
								// consistency is preserved. If the method relied on calculating the current
								// fighter on the fly, this would not work, since calling code can update
								// fighter states.

	/**
	 * Gets the fighter that will make the next move (the fighter whose turn it is).
	 * 
	 * @return The fighter.
	 */
	public F getCurrentFighter() {
		return currentFighter;
	}

	/**
	 * <p>
	 * Completes an action performed by the {@link #getCurrentFighter() current
	 * fighter} and updates the state of this {@link Battle} so that it is
	 * consistent with the specifiction of battles for the next turn. (Essentially,
	 * this method prepares the state of this object so that calling code can
	 * perform the next {@link Fighter}'s move.)
	 * </p>
	 * <p>
	 * This method performs three characteristic operations:
	 * <ol>
	 * <li>The elimination and revival of any {@link Fighter}s (since the last
	 * {@link Fighter}'s move) according to each {@link Fighter}'s health.
	 * <ul>
	 * <li>Specifically, each {@link Fighter} whose health <code>&lt;= 0</code> is
	 * considered eliminated and each {@link Fighter} whose health changed to become
	 * <code>&gt; 0</code> is then considered alive (or revived).</li>
	 * </ul>
	 * </li>
	 * <li>If the {@link #getCurrentFighter() current fighter} was not eliminated,
	 * adds the specified number of ticks that its move took to its ticks.</li>
	 * <li>Sorts and normalizes the {@link #getBattleQueue() battle queue}, so that
	 * it is ordered ascendingly and so that the first {@link Fighter} in it, (i.e.,
	 * the {@link Fighter} at position <code>0</code>), has <code>0</code> ticks.
	 * This causes {@link #getCurrentFighter() the current fighter} to be updated to
	 * reflect whatever {@link Fighter} has the fewest number of ticks after the
	 * conclusion of the previous {@link Fighter}'s move.</li>
	 * </ol>
	 * </p>
	 * 
	 * @param ticks The number of ticks that the {@link Fighter}'s action took.
	 */
	public void act(int ticks) {
		// Remove all dead fighters.
		for (Iterator<F> iterator = battleQueue.iterator(); iterator.hasNext();) {
			F f = iterator.next();
			if (f.isFainted()) {
				iterator.remove();
				ticksTillTurn.remove(f);
			}
		}

		// Here we sort the fighter queue, *then* get the last fighter in the fighter
		// queue (whose ticks we will need later for any revived fighters), *then* we
		// normalize the fighter queue.

		// We sort the fighter queue because this is a requirement (see method doc;
		// characteristic operation #4), and because it makes it easier to determine who
		// the last fighter in the queue was while the acting fighter was acting: The
		// last fighter in the queue now is that last fighter.

		// Once we have the last fighter, we normalize the entire battle queue now, so
		// that we won't have to later. (When we add new, revived fighters into the
		// battle, we add them such that their ticks are the "current" last fighter's
		// ticks + 1. We don't want to add a bunch of new fighters and then have to
		// normalize them too. If we normalize the queue first, when we add them,
		// they'll be normalized already.)

		// After we normalize, we get the last fighter's ticks. This is the number of
		// ticks that we use to calculate where revived fighters will go. This is
		// because we want revived fighters to appear as if they were revived "during"
		// the #currentFighter's turn, so if the #currentFighter gets to the end of the
		// queue as a result of its move, all the revived fighters will go before it
		// (unless it ties with what was previously there). If a deeper explanation is
		// desired then contact me!
		sort();
		var maxf = battleQueue.get(battleQueue.size() - 1);// Get last fighter, (has the most ticks).
		setTicks(getCurrentFighter(), getTicks(getCurrentFighter()) + ticks);
		battleQueue.remove(getCurrentFighter());
		battleQueue.add(-Collections.binarySearch(battleQueue, getCurrentFighter(), sortingComparator()) - 1,
				getCurrentFighter());
		normalize();
		var max = ticksTillTurn.get(maxf);

		// Add all revived fighters.
		for (var t : teams)
			for (var f : t)// For every fighter,
				if (!f.isFainted() && !ticksTillTurn.containsKey(f)) {// If the fighter is alive but is not in the ticks
																		// map,
					// Add the fighter to the ticks map and add the fighter to the battle queue.
					ticksTillTurn.put(f, max + 1);
					// TODO Possibly use sublist method to reduce the number of fighters sorted
					// through.
					battleQueue.add(-Collections.binarySearch(battleQueue, f, sortingComparator()) - 1, f);
				}

		// Battle queue is now normalized, sorted, and dead/revived fighters are
		// accounted for. Update #currentFighter.
		currentFighter = battleQueue.get(0);
	}

	private final Map<F, Integer> ticksTillTurn = new HashMap<>();
	private final List<F> battleQueue = new ArrayList<>();
	private final Set<T> teams, remainingTeams = new HashSet<>();

	public Map<F, Integer> getTicksTillTurn() {
		return Collections.unmodifiableMap(ticksTillTurn);
	}

	public List<F> getBattleQueue() {
		return Collections.unmodifiableList(battleQueue);
	}

	public Set<T> getTeams() {
		return Collections.unmodifiableSet(teams);
	}

	public Set<T> getRemainingTeams() {
		return Collections.unmodifiableSet(remainingTeams);
	}

	private void start() {
		remainingTeams.clear();
		remainingTeams.addAll(teams);

		Collections.sort(battleQueue, (o1, o2) -> Integer.compare(ticksTillTurn.get(o1), ticksTillTurn.get(o2)));
		assignInitialTicks(battleQueue);

	}

	protected void assignInitialTicks(List<F> queue) {
		// Assign initial ticks.
		var max = battleQueue.get(0).getSpeed();

		for (F f : queue)
			ticksTillTurn.put(f, new BigDecimal(max.subtract(f.getSpeed()))
					.multiply(BigDecimal.valueOf(Math.random() / 5 + 0.9)).intValue());
	}

	protected final void setTicks(F fighter, int ticks) {
		ticksTillTurn.put(fighter, ticks);
	}

	protected final int getTicks(F fighter) {
		return ticksTillTurn.get(fighter);
	}

	/**
	 * Sorts the battle queue according to ticks. This is automatically done at the
	 * conclusion of every turn (by {@link #act(int)}), specifically after the ticks
	 * to the {@link Fighter} that has just performed its action have been applied
	 * to it.
	 */
	protected final void sort() {
		Collections.sort(battleQueue, sortingComparator());
	}

	protected final Comparator<F> sortingComparator() {
		return (o1, o2) -> {
			int tickcomp = Integer.compare(ticksTillTurn.get(o1), ticksTillTurn.get(o2));
			return tickcomp == 0 ? o1.compareTo(o2) : tickcomp;
		};
	}

	protected final void normalize() {
		var ticks = getTicks(battleQueue.get(0));
		if (ticks != 0)
			for (var e : ticksTillTurn.entrySet())
				e.setValue(e.getValue() - ticks);
	}

	@SafeVarargs
	public Battle(T... teams) {
		this.teams = new HashSet<>();
		for (var t : teams) {
			this.teams.add(t);
			for (var f : t) {
				int pos = Collections.binarySearch(battleQueue, f, Comparator.<F>naturalOrder().reversed());
				battleQueue.add(pos >= 0 ? pos : -pos - 1, f);
			}
		}
		start();
	}

	public Battle(Collection<T> teams) {
		this.teams = new HashSet<>(teams);
		for (var t : teams)
			for (var f : t) {
				int pos = Collections.binarySearch(battleQueue, f, Comparator.<F>naturalOrder().reversed());
				battleQueue.add(pos >= 0 ? pos : -pos - 1, f);
			}
		start();
	}

	/**
	 * <p>
	 * Gets the {@link Team} that the provided {@link Fighter} belongs to by
	 * searching each {@link Team} until the specified {@link Fighter} is found in
	 * one. If no {@link Team} contains the specified {@link Fighter}, this method
	 * returns <code>null</code>.
	 * </p>
	 * <p>
	 * This method searches {@link #getTeams() every Team in this Battle},
	 * <i>not</i> just {@link #getRemainingTeams() remaining team}s.
	 * </p>
	 * 
	 * @param f0 The {@link Fighter} to get the {@link Team} of.
	 * @return The {@link Team} that the provided {@link Fighter} belongs to, or
	 *         <code>null</code> if such {@link Team} is not tracked by this
	 *         {@link Battle}.
	 */
	public final T getTeam(F f0) {
		for (var t : teams)
			if (t.contains(f0))
				return t;
		return null;
	}

	/**
	 * <p>
	 * The order that this documentation refers to and that this method conforms to
	 * is the order of the battle queue, i.e., the list of remaining
	 * {@link Fighter}s ordered by ticks, (in ascending order: fewer ticks means
	 * closer to index <code>0</code>).
	 * </p>
	 * <p>
	 * This method returns the {@link Fighter} that is the
	 * <code>n</code><sup>th</sup> {@link Fighter}, sorted ascendingly by ticks, of
	 * the {@link Fighter}s that are not in the same {@link Team} as the target
	 * {@link Fighter}.
	 * </p>
	 * <p>
	 * Simply put, this method returns the nth opponent (this does not include
	 * {@link Fighter}s on the same {@link Team}) {@link Fighter} in the battle
	 * queue.
	 * </p>
	 * 
	 * @param n    The indexing parameter, used to specify which opponent should be
	 *             selected.
	 * @param targ The target {@link Fighter}.
	 * @return The selected {@link Fighter}.
	 */
	public final F getNextNthOpponent(int n, F targ) {
		return getNextNthOpponent(n, getTeam(targ));
	}

	/**
	 * <p>
	 * The order that this documentation refers to and that this method conforms to
	 * is the order of the battle queue, i.e., the list of remaining
	 * {@link Fighter}s ordered by ticks, (in ascending order: fewer ticks means
	 * closer to index <code>0</code>).
	 * </p>
	 * <p>
	 * This method returns the {@link Fighter} that is the
	 * <code>n</code><sup>th</sup> {@link Fighter}, sorted ascendingly by ticks, of
	 * the {@link Fighter}s that are not in the provided {@link Team}.
	 * </p>
	 * <p>
	 * Simply put, this method returns the nth opponent (meaning exclusive of
	 * {@link Fighter}s in the provided {@link Team}) {@link Fighter} of the
	 * provided {@link Team}, in the battle queue.
	 * </p>
	 * 
	 * @param n    The indexing parameter, used to specify which opponent should be
	 *             selected.
	 * @param targ The targeted {@link Team}.
	 * @return The selected {@link Fighter}, or <code>null</code> if no matching
	 *         {@link Fighter} is found for some reason.
	 */
	public final F getNextNthOpponent(int n, T targ) {
		for (var f : battleQueue)
			if (!targ.contains(f))
				if (n-- == 0)
					return f;
		return null;
	}

	/**
	 * Returns the number of living {@link Fighter}s still participating in this
	 * {@link Battle}. This method returns the number of living fighters in this
	 * {@link Battle} at the last moment in which the battle was in a valid state
	 * (right after construction or right after a call to {@link #act(int)}).
	 * 
	 * @return The number of {@link Fighter}s in this {@link Battle}.
	 */
	public final int getFighterCount() {
		return battleQueue.size();
	}

	public List<F> getRemainingFighters(T team) {
		List<F> fighters = new ArrayList<>();
		for (F f : team)
			if (!f.isFainted())
				fighters.add(f);
		return fighters;
	}

	// Closed until manual tracking of surrendered teams is made (so that teams can
	// "temporarily surrender").
//	/**
//	 * Causes the specified {@link Team} to forfeit the battle. This will result in
//	 * a win for the opposing {@link Team}, if there is only one opposing
//	 * {@link Team} remaining. Otherwise, any remaining {@link Team}s will continue
//	 * battle.
//	 * 
//	 * @param team The {@link Team} to surrender.
//	 */
//	protected final void surrender(T team) {
//		for (Iterator<F> iterator = battleQueue.iterator(); iterator.hasNext();)
//			if (team.contains(iterator.next()))
//				iterator.remove();
//		teamLose(team);
//	}

	/**
	 * Returns the winning {@link Team} if this {@link Battle} is over and is not a
	 * draw. Otherwise, returns <code>null</code>.
	 * 
	 * @return The winning {@link Team}, if there currently is one.
	 */
	public final T getWinningTeam() {
		return remainingTeams.size() == 1 ? remainingTeams.iterator().next() : null;
	}

	/**
	 * Returns <code>true</code> if this {@link Battle} is over and is a draw.
	 * <code>false</code> otherwise.
	 * 
	 * @return if this {@link Battle} is over and is a draw.
	 */
	public final boolean isDraw() {
		return remainingTeams.isEmpty();
	}

}
