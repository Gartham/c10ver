package gartham.c10ver.games.rpg.fighting.battles.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public abstract class Battle<A, F extends Fighter, T extends Team<F>> {

	private final Map<F, Integer> ticksTillTurn = new HashMap<>();
	private final List<F> battleQueue = new ArrayList<>();
	private final List<T> teams;
	private State state = State.UNSTARTED;

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
	 */
	public final void act(A action) {
		if (state != State.RUNNING)
			throw new IllegalStateException("Battles must be in a running state for actions to be taken.");
		var fighter = battleQueue.get(0);
		var t = handleAction(action, fighter);
		ticksTillTurn.put(fighter, ticksTillTurn.get(fighter) + t);// We get the ticks for our fighter because the
																	// action taken my have modified its ticks via
																	// side-effect.
		// Finally we re-sort the battle queue.
		sortQueue();

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
	 * @return The number of ticks that the action has taken.
	 */
	protected abstract int handleAction(A action, F fighter);

	/**
	 * Sorts the battle queue according to ticks. This is automatically done at the
	 * conclusion of every turn, specifically after the ticks to the {@link Fighter}
	 * that has just performed its action have been applied to it.
	 */
	protected final void sortQueue() {
		Collections.sort(battleQueue, (o1, o2) -> Integer.compare(ticksTillTurn.get(o1), ticksTillTurn.get(o2)));
	}

	@SafeVarargs
	public Battle(T... teams) {
		this.teams = new ArrayList<>();
		for (var t : teams) {
			this.teams.add(t);
			for (var f : t)
				battleQueue.add(-Collections.binarySearch(battleQueue, f, Comparator.<F>naturalOrder().reversed()) - 1,
						f);
		}
	}

	public Battle(Collection<T> teams) {
		this.teams = new ArrayList<>(teams);
		for (var t : teams)
			for (var f : t)
				battleQueue.add(-Collections.binarySearch(battleQueue, f, Comparator.<F>naturalOrder().reversed()) - 1,
						f);
	}

	public void stop() {
		state = State.STOPPED;
	}

}
