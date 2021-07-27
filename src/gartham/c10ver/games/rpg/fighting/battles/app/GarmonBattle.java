package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.Collection;

import gartham.c10ver.games.rpg.fighting.battles.api.Battle;
import gartham.c10ver.games.rpg.fighting.battles.api.Team;

public class GarmonBattle extends Battle<GarmonBattleAction, GarmonFighter, Team<GarmonFighter>> {

	public GarmonBattle(Collection<Team<GarmonFighter>> teams) {
		super(teams);
	}

	@SafeVarargs
	public GarmonBattle(Team<GarmonFighter>... teams) {
		super(teams);
	}

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
			var ticks = getTicks(getNextNthOpponent(0, fighter));
			setTicks(fighter, ticks);
			break;

		case SPECIAL_ATTACK:
			var attack = action.getSpecialAttack();
			attack.getTarget().damage(attack.getDamage());
			break;

		default:
			break;
		}
		return 0;
	}

}
