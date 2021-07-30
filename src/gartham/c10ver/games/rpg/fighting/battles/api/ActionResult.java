package gartham.c10ver.games.rpg.fighting.battles.api;

public class ActionResult {
	private final boolean battleOver;

	public ActionResult(boolean battleOver) {
		this.battleOver = battleOver;
	}

	public boolean isBattleOver() {
		return battleOver;
	}
}
