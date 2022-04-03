package gartham.c10ver.games.rpg.fighting.battles.api;

import java.util.Collection;
import java.util.function.Consumer;

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

	/**
	 * <p>
	 * Executes this {@link ControlledBattle} from start to finish on a separate
	 * {@link Thread}. The thread is created by this method and is returned. At the
	 * end of the battle, the provided {@link Consumer} is called, if the argument
	 * is not <code>null</code>, and the winning team (if any) is passed to the
	 * {@link Consumer}. If there is no winning team, i.e., the
	 * {@link ControlledBattle} is a draw, then <code>null</code> is provided to the
	 * {@link Consumer}. The {@link Consumer} is always called on the thread spawned
	 * by this method (the thread the battle executes on).
	 * 
	 * @param daemon     Whether the thread executing the {@link Battle} should be a
	 *                   daemon thread or not.
	 * @param winHandler The {@link Consumer} to be called when the battle is over.
	 * @return The {@link Thread} on which the game is executing.
	 */
	public Thread startAsync(boolean daemon, Consumer<T> winHandler) {
		var t = new Thread(() -> {
			start();
			if (winHandler != null)
				winHandler.accept(isDraw() ? null : getWinningTeam());
		});
		t.setDaemon(daemon);
		t.start();
		return t;
	}

	/**
	 * <p>
	 * Executes this {@link ControlledBattle} from start to finish on a separate
	 * {@link Thread}. The thread is created by this method and is returned. At the
	 * end of the battle, the provided {@link Consumer} is called, if the argument
	 * is not <code>null</code>, and the winning team (if any) is passed to the
	 * {@link Consumer}. If there is no winning team, i.e., the
	 * {@link ControlledBattle} is a draw, then <code>null</code> is provided to the
	 * {@link Consumer}. The {@link Consumer} is always called on the thread spawned
	 * by this method (the thread the battle executes on).
	 * 
	 * @param daemon     Whether the thread executing the {@link Battle} should be a
	 *                   daemon thread or not.
	 * @param winHandler The {@link Consumer} to be called when the battle is over.
	 * @return The {@link Thread} on which the game is executing.
	 */
	public Thread startAsync(boolean daemon, Consumer<T> winHandler, long delayMillis) {
		var t = new Thread(() -> {
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			start();
			if (winHandler != null)
				winHandler.accept(isDraw() ? null : getWinningTeam());
		});
		t.setDaemon(daemon);
		t.start();
		return t;
	}

}
