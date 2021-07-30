package gartham.c10ver.games.rpg.fighting.battles.api;

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public class ActionCompletion<R extends ActionResult, F extends Fighter> {

	private final boolean battleOver;
	private final F fighter;
	private final R result;

	public ActionCompletion(boolean battleOver, F fighter, R result) {
		this.battleOver = battleOver;
		this.fighter = fighter;
		this.result = result;
	}

	public boolean isBattleOver() {
		return battleOver;
	}

	public R getResult() {
		return result;
	}

	public int getTicks() {
		return result.getTicks();
	}

	public F getFighter() {
		return fighter;
	}

}
