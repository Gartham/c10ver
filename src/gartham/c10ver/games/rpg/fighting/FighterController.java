package gartham.c10ver.games.rpg.fighting;

import gartham.c10ver.games.rpg.fighting.battles.Battle;

public interface FighterController {
	/**
	 * Controls the {@link Fighter} linked to this {@link FighterController} for its
	 * turn and then invokes {@link Battle#nextTurn()} when finished.
	 */
	void act();
}
