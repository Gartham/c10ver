package gartham.c10ver.games.rpg.fighting.battles.app;

import gartham.c10ver.games.rpg.fighting.battles.api.Battle;
import gartham.c10ver.games.rpg.fighting.battles.api.Team;
import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public class GarmonBattle extends Battle<GarmonBattleAction, GarmonFighter, Team<GarmonFighter>> {

	@Override
	protected int handleAction(GarmonBattleAction action, GarmonFighter fighter) {
		switch (action.getType()) {
		case ATTACK:
			var att = action.getSpecialAttack();
			att.getTarget().damage(att.getDamage());
			break;

		case SKIP_TURN:
			// Set ticks to be equal to the next OPPONENT in line + 1.
			// We need some way to get the next opponent.

		default:
			break;
		}
		return 0;
	}

}
