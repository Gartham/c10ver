package gartham.c10ver.games.rpg.fighting.battles.api;

public class ActionResult {
	private final int ticks;

	public ActionResult(int ticks) {
		this.ticks = ticks;
	}

	/**
	 * Returns the number of ticks that the action that this {@link ActionResult}
	 * represents the result of took.
	 * 
	 * @return The number of ticks taken by the action.
	 */
	public int getTicks() {
		return ticks;
	}

}
