package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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

	private static final BigInteger max(BigInteger first, BigInteger second) {
		return first.compareTo(second) > 0 ? first : second;
	}

	@Override
	protected int handleAction(GarmonBattleAction action, GarmonFighter fighter) {
		switch (action.getType()) {
		case ATTACK:
			// TODO
			break;

		case SKIP_TURN:
			// Set ticks to be equal to the next OPPONENT in line + 1.
			// We need some way to get the next opponent.
			var ticks = getTicks(getNextNthOpponent(0, fighter));
			setTicks(fighter, ticks);
			break;

		case SPECIAL_ATTACK:
			var att = action.getSpecialAttack();
			att.getTarget()
					.damage(max(BigInteger.ZERO,
							att.getPower().multiply(new BigDecimal(fighter.getAttack()))
									.subtract(new BigDecimal(att.getTarget().getDefense()))
									.setScale(0, RoundingMode.HALF_UP).toBigInteger()));
			break;

		default:
			break;
		}
		return 0;
	}

}
