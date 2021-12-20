package gartham.c10ver.games.rpg.fighting.battles.api;

import java.util.Collection;

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public abstract class ControlledBattle<F extends Fighter, T extends Team<F>> extends Battle<F, T> {

	public ControlledBattle(Collection<T> teams) {
		super(teams);
	}

	@SafeVarargs
	public ControlledBattle(T... teams) {
		super(teams);
	}

	protected abstract Controller<F> getController(F fighter);

	/**
	 * Starts this battle on the executing thread. This method does not return until
	 * the battle is completed. This method calls {@link #nextTurn()} until the
	 * battle has concluded.
	 */
	public void start() {
		while (!isOver())
			nextTurn();
	}

	/**
	 * <p>
	 * Executes the next turn. This method should not be called if the battle
	 * {@link #isOver()}
	 * </p>
	 * <p>
	 * This method calls the controller of the next fighter to execute that
	 * fighter's turn, and then calls {@link #act(int)} to ready this {@link Battle}
	 * object for the next fighter's turn.
	 * </p>
	 */
	public void nextTurn() {
		F fighter = getCurrentFighter();
		act(getController(fighter).move(fighter));
	}

	public Thread startAsync(boolean daemon) {
		var t = new Thread(this::start);
		t.setDaemon(daemon);
		t.start();
		return t;
	}
}
