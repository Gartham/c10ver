package gartham.c10ver.games.rpg.fighting;

public interface FighterController {
	/**
	 * Controlls the {@link Fighter} linked to this {@link FighterController} for
	 * its turn, and returns the number of ticks that the action taken takes.
	 * 
	 * @return The number of ticks taken by the action.
	 */
	int act();
}
