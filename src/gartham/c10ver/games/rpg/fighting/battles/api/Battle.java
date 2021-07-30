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
 * dead, the battle is considered a draw between <b>all participating
 * {@link Team}s</b>.
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
public abstract class Battle<A, F extends Fighter, T extends Team<F>, R extends ActionResult> {

	private final Map<F, Integer> ticksTillTurn = new HashMap<>();
	private final List<F> battleQueue = new ArrayList<>();
	private final Set<T> teams, remainingTeams = new HashSet<>();
	private State state = State.UNSTARTED;

	private final Map<F, Integer> ticksTillTurnUnmodifiable = Collections.unmodifiableMap(ticksTillTurn);
	private final List<F> battleQueueUnmodifiable = Collections.unmodifiableList(battleQueue);
	private final Set<T> teamsUnmodifiable, remainingTeamsUnmodifiable = Collections.unmodifiableSet(remainingTeams);

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

	public enum State {
		/**
		 * Denotes that a {@link Battle} is yet to be started.
		 */
		UNSTARTED,
		/**
		 * Denotes that a battle has been started via {@link Battle#start()}.
		 */
		RUNNING,
		/**
		 * Denotes that a {@link Battle} has been stopped, meaning fighting has
		 * concluded. This can either be through its {@link Battle#stop()} method or
		 * through a {@link Team} naturally winning.
		 */
		STOPPED;
	}

	public final State getState() {
		return state;
	}

	/**
	 * Starts this {@link Battle} by setting the {@link #state} to
	 * {@link State#RUNNING} and assigning <b>initial ticks</b> to each
	 * {@link Fighter}.
	 */
	public void start() {
		if (state != State.UNSTARTED)
			throw new IllegalStateException("Battles cannot be started more than once.");
		state = State.RUNNING;
		remainingTeams.clear();
		remainingTeams.addAll(teams);

		assignInitialTicks(battleQueue);

		Collections.sort(battleQueue, (o1, o2) -> Integer.compare(ticksTillTurn.get(o1), ticksTillTurn.get(o2)));
	}

	/**
	 * <p>
	 * This is the method that assigns <b>initial ticks</b> to each {@link Fighter}.
	 * Upon a call to this method by {@link #start()}, the provided
	 * <code>queue</code> of {@link Fighter}s (1) contains all of the
	 * {@link Fighter}s participating in the {@link Battle} and (2) is sorted, in
	 * descending order, by {@link Fighter#getSpeed() speed}. This means that the
	 * {@link Fighter} with the highest speed is positioned at index <code>0</code>.
	 * </p>
	 * <p>
	 * This method is <b>only</b> tasked with setting the number of ticks for each
	 * {@link Fighter} through the {@link #setTicks(Fighter, int)} method. The
	 * provided {@link List} is unmodifiable, as the {@link #battleQueue battle
	 * queue} used by {@link Battle} is sorted according to ticks immediately after
	 * this method is called by {@link #start()}.
	 */
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
	public final ActionCompletion<R> act(A action) {
		if (state != State.RUNNING)
			throw new IllegalStateException("Battles must be in a running state for actions to be taken.");
		var fighter = getActingFighter();
		var t = handleAction(action, fighter);
		ticksTillTurn.put(fighter, ticksTillTurn.get(fighter) + t.getTicks());// We get the ticks for our fighter
																				// because the
		// action taken my have modified its ticks via
		// side-effect.
		if (state == State.STOPPED)
			return new ActionCompletion<>(true, t);

		// Finally we re-sort the battle queue.
		sortQueue();
		shiftQueue();
		return new ActionCompletion<>(false, t);

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
		var ticks = getTicks(getActingFighter());
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

	private void checkForNaturalTerm() {
		if (remainingTeams.size() <= 1)
			state = State.STOPPED;
	}

	private void teamLose(T team) {
		remainingTeams.remove(team);
		checkForNaturalTerm();
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
