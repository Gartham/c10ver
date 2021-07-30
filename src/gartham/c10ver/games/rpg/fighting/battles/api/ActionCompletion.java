package gartham.c10ver.games.rpg.fighting.battles.api;

public class ActionCompletion<R extends ActionResult> {

	private final boolean battleOver;
	private final R result;

	public ActionCompletion(boolean battleOver, R result) {
		this.battleOver = battleOver;
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

}
