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
 * <h1>Introduction</h1><i>- A brief overview of the <b>concept</b> of
 * battles.</i>
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
 * <h1>Class Model</h1>
 * <p>
 * This class models a battle, as described above. It is constructed with at
 * least two {@link Team}s and it begins in its initial state (awaiting the
 * first fighter's move) immediately after construction.
 * </p>
 * <p>
 * This class manages the <b>ticks</b> and <b>liveliness</b> of each fighter,
 * (more specifically, it keeps track of a {@link #getBattleQueueUnmodifiable()
 * battle queue}), such that the following properties hold at all times except
 * during the execution of a move, (as this class is not synchronized):
 * <ol>
 * <li>The {@link #getBattleQueueUnmodifiable() battle queue} is comprised only
 * of every living {@link Fighter}.</li>
 * <li>The {@link #getBattleQueueUnmodifiable() battle queue} is sorted in
 * ascending order, with the first element having the fewest ticks.</li>
 * <li>The first element of the {@link #getBattleQueueUnmodifiable() battle
 * queue} has exactly <code>0</code> ticks.</li>
 * </ol>
 * </p>
 * <p>
 * This class does not report the effects of {@link Fighter}s' moves to calling
 * code. When calling code affects the state of the {@link Battle} in some way,
 * it is expected to determine the effects.
 * </p>
 * 
 * @author Gartham
 *
 * @param <A> The type representing the actions that can be taken by
 *            {@link Fighter}s tracked by this {@link Battle}.
 */
public abstract class Battle<A, F extends Fighter, T extends Team<F>, R extends ActionResult> {

	/**
	 * Gets the fighter that will make the next move (the fighter whose turn it is).
	 * 
	 * @return The fighter.
	 */
	public F getCurrentFighter();

	/**
	 * Completes an action performed by the {@link #getCurrentFighter() current
	 * fighter} by adding the specified number of ticks to that fighter and then
	 * updating the state by calling {@link #updateState()}.
	 * 
	 * @param ticks The number of ticks that the {@link Fighter}'s action took.
	 */
	public void act(int ticks);

	/**
	 * <p>
	 * Updates the state of this {@link Battle} so that it is consistent with the
	 * specification of battles for the next turn. This method is called by
	 * {@link #act(int)}.
	 * </p>
	 * <p>
	 * This method performs the following operations so that the
	 * {@link #getBattleQueueUnmodifiable() battle queue} and
	 * {@link #getTicksTillTurnUnmodifiable() fighters' ticks} are consistent with
	 * the specification:
	 * <ol>
	 * <li>Updates the {@link #getBattleQueueUnmodifiable() battle queue} so that it
	 * contains only living {@link Fighter}s (and all the living {@link Fighter}s).
	 * This is done by removing from it any {@link Fighter}s eliminated since the
	 * last call to {@link #updateState()} and adding any {@link Fighter}s whose
	 * health(s) rose above <code>0</code> since. Any {@link Fighter}s added back to
	 * the {@link Battle} like this are positioned at the end of the
	 * {@link #getBattleQueueUnmodifiable() battle queue}, and is given the same
	 * number of ticks as the {@link Fighter} previously at the end.</li>
	 * <li>Updates the list of {@link #getRemainingTeamsUnmodifiable() remaining
	 * teams} so that only teams with extant, (still-living) {@link Fighter}s are
	 * contained within it, and that all such teams are contained within it.</li>
	 * <li>Sorts the {@link #getBattleQueueUnmodifiable() battle queue} so that the
	 * {@link Fighter} at position <code>0</code> has the lowest number of ticks and
	 * the {@link Fighter} at the end has the highest.</li>
	 * <li>Normalizes the {@link #getBattleQueueUnmodifiable() battle queue} so that
	 * the {@link Fighter} at position <code>0</code> has <code>0</code> ticks. This
	 * is done by finding the ticks of the {@link Fighter} at position
	 * <code>0</code> and subtracting that amount from the ticks of every fighter in
	 * the {@link #getBattleQueueUnmodifiable() battle queue}.</li>
	 * </ol>
	 * </p>
	 */
	public void updateState();

	private final Map<F, Integer> ticksTillTurn = new HashMap<>();
	private final List<F> battleQueue = new ArrayList<>();
	private final Set<T> teams, remainingTeams = new HashSet<>();

	private Map<F, Integer> ticksTillTurnUnmodifiable = Collections.unmodifiableMap(ticksTillTurn);
	private List<F> battleQueueUnmodifiable = Collections.unmodifiableList(battleQueue);
	private Set<T> teamsUnmodifiable, remainingTeamsUnmodifiable = Collections.unmodifiableSet(remainingTeams);

	public final Map<F, Integer> getTicksTillTurnUnmodifiable() {
		return ticksTillTurnUnmodifiable;
	}

	public final List<F> getBattleQueueUnmodifiable() {
		return battleQueueUnmodifiable;
	}

	public final Set<T> getTeamsUnmodifiable() {
		return teamsUnmodifiable;
	}

	public final Set<T> getRemainingTeamsUnmodifiable() {
		return remainingTeamsUnmodifiable;
	}

	public void start() {
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
	 * Acts as the current {@link Fighter}.
	 * 
	 * @param action The action to take.
	 * @return <code>true</code> if the {@link Battle} is over.
	 */
	public final ActionCompletion<R, F> act(A action) {
		var fighter = getCurrentFighter();
		var t = handleAction(action, fighter);
		ticksTillTurn.put(fighter, ticksTillTurn.get(fighter) + t.getTicks());// We get the ticks for our fighter
																				// because the
		// action taken my have modified its ticks via
		// side-effect.
		if (state == State.STOPPED)
			return new ActionCompletion<>(true, fighter, t);

		// Finally we re-sort the battle queue.
		sortQueue();
		shiftQueue();
		return new ActionCompletion<>(false, fighter, t);

	}

	/**
	 * Performs any unique behavior specified by the provided <code>action</code>,
	 * and returns the number of ticks that the action has taken. This method should
	 * <b>not</b> add said number of ticks to the {@link Fighter} that performed the
	 * action. That is handled by {@link #act(Object)} in the {@link Battle} class.
	 * 
	 * @param action  The action taken.
	 * @param fighter The {@link Fighter} that took the action (i.e. the current
	 *                fighter). At the beginning of this method call, (as per normal
	 *                {@link Battle} behavior), this argument should be exactly the
	 *                same as the {@link Fighter} in the front (position
	 *                <code>0</code>) of the {@link #battleQueue battle queue}.
	 * @return An {@link ActionResult} object containing the number of ticks that
	 *         the action took as well as any other information used by this battle.
	 */
	protected abstract R handleAction(A action, F fighter);

	/**
	 * Sorts the battle queue according to ticks. This is automatically done at the
	 * conclusion of every turn, specifically after the ticks to the {@link Fighter}
	 * that has just performed its action have been applied to it.
	 */
	protected final void sortQueue() {
		Collections.sort(battleQueue, sortingComparator());
	}

	protected final Comparator<F> sortingComparator() {
		return (o1, o2) -> {
			int tickcomp = Integer.compare(ticksTillTurn.get(o1), ticksTillTurn.get(o2));
			return tickcomp == 0 ? o1.compareTo(o2) : tickcomp;
		};
	}

	protected final void shiftQueue() {
		var ticks = getTicks(getCurrentFighter());
		if (ticks != 0)
			for (var e : ticksTillTurn.entrySet())
				e.setValue(e.getValue() - ticks);
	}

	@SafeVarargs
	public Battle(T... teams) {
		teamsUnmodifiable = Collections.unmodifiableSet(this.teams = new HashSet<>());
		for (var t : teams) {
			this.teams.add(t);
			for (var f : t)
				battleQueue.add(-Collections.binarySearch(battleQueue, f, Comparator.<F>naturalOrder().reversed()) - 1,
						f);
		}
	}

	public Battle(Collection<T> teams) {
		teamsUnmodifiable = Collections.unmodifiableSet(this.teams = new HashSet<>(teams));
		for (var t : teams)
			for (var f : t)
				battleQueue.add(-Collections.binarySearch(battleQueue, f, Comparator.<F>naturalOrder().reversed()) - 1,
						f);
	}

	public void stop() {
		state = State.STOPPED;
	}

	public final F getActingFighter() {
		return battleQueue.get(0);
	}

	/**
	 * Gets the {@link Team} that the provided {@link Fighter} belongs to.
	 * 
	 * @param f0 The {@link Fighter} to get the {@link Team} of.
	 * @return The {@link Team} that the provided {@link Fighter} belongs to.
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
	 * {@link Battle}.
	 * 
	 * @return The number of {@link Fighter}s in this {@link Battle}.
	 */
	public final int getFighterCount() {
		return battleQueue.size();
	}

	public List<F> getRemainingFighters(T team) {
		List<F> fighters = new ArrayList<>();
		for (F f : battleQueue)
			if (!f.isFainted() && team.contains(f))
				fighters.add(f);
		return fighters;
	}

	/**
	 * Causes the specified {@link Team} to forfeit the battle. This will result in
	 * a win for the opposing {@link Team}, if there is only one opposing
	 * {@link Team} remaining. Otherwise, any remaining {@link Team}s will continue
	 * battle.
	 * 
	 * @param team The {@link Team} to surrender.
	 */
	protected final void surrender(T team) {
		for (Iterator<F> iterator = battleQueue.iterator(); iterator.hasNext();)
			if (team.contains(iterator.next()))
				iterator.remove();
		teamLose(team);
	}

	/**
	 * Returns the winning {@link Team} if this {@link Battle} is over and is not a
	 * draw. Otherwise, returns <code>null</code>.
	 * 
	 * @return The winning {@link Team}, if there is one.
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

	/**
	 * <p>
	 * Removes the specified {@link Fighter} from this {@link Battle}. This is done
	 * whenever a {@link Fighter} "faints" or otherwise loses and can no longer
	 * participate in {@link Battle}. Removal from a {@link Battle} is signified by
	 * a lack of the {@link Fighter}'s presence in the battle queue. The
	 * {@link Fighter} will still remain in its {@link Team}, as {@link Team}s are
	 * immutable, however if all the {@link Fighter}s in a {@link Team} are
	 * 
	 * @param fighter The {@link Fighter} to remove.
	 */
	protected final void remove(F fighter) {
		battleQueue.remove(fighter);
		T team = getTeam(fighter);
		for (F f : team)
			if (!f.isFainted())
				return;
		teamLose(team);
	}

}
