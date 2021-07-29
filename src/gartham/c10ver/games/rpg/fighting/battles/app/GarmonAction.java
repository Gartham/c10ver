package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.function.Consumer;

import gartham.c10ver.actions.Action;
import gartham.c10ver.actions.ActionInvocation;
import gartham.c10ver.games.rpg.fighting.battles.app.GarmonBattleAction.ActionType;

public class GarmonAction extends Action {

	// TODO Make constructors based off of action types

	private final String description;

	/**
	 * Consntructs a {@link GarmonAction} option that is either surrendering or
	 * skipping turns.
	 * 
	 * @param surrender Whether this {@link Action} will be to surrender or skip the
	 *                  turn.
	 */
	public GarmonAction(boolean surrender, GarmonBattle battle) {
		super(surrender ? "\uD83C\uDFF3" : "\uD83D\uDCA8", surrender ? "Surrender" : "Skip Turn",
				surrender ? new Consumer<>() {

					@Override
					public void accept(ActionInvocation t) {
						battle.act(new GarmonBattleAction(ActionType.SURRENDER));
					}
				} : new Consumer<>() {

					@Override
					public void accept(ActionInvocation t) {
						battle.act(new GarmonBattleAction(ActionType.SKIP_TURN));
						// TODO battleManager.next();
					}
				});
		description = surrender ? "Give up the battle and take the L." : "Pass up this creature's move.";
	}

	public GarmonAction(String emoji, String name, String description, Consumer<ActionInvocation> action) {
		super(emoji, name, action);
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
