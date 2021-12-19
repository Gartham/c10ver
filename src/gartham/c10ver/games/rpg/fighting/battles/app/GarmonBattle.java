package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.Collection;

import gartham.c10ver.games.rpg.fighting.battles.api.Battle;

public class GarmonBattle extends Battle<GarmonFighter, GarmonTeam> {

	public GarmonBattle(Collection<GarmonTeam> teams) {
		super(teams);
	}

	public GarmonBattle(GarmonTeam... teams) {
		super(teams);
	}

	public int getFighterTicks(GarmonFighter fighter) {
		return getTicks(fighter);
	}

//	@Override
//	protected GarmonActionResult handleAction(GarmonBattleAction action, GarmonFighter fighter) {
//		switch (action.getType()) {
//		case ATTACK:
//			BigInteger attack = fighter.getAttack();
//			for (int i = 2; i < 13; i++)
//				if (rand.nextInt(i) == 0)
//					attack = attack.add(fighter.getAttack().divide(BigInteger.valueOf(i + 1)));
//			BigInteger dmg = max(BigInteger.ZERO, attack.subtract(action.getTarget().getDefense()));
//			action.getTarget().damage(dmg);
//			if (action.getTarget().isFainted())
//				remove(action.getTarget());
//			return new GarmonActionResult(50, action.getType(), dmg, action.getTarget());
//
//		case SKIP_TURN:
//			// Set ticks to be equal to the next OPPONENT in line + 1.
//			// We need some way to get the next opponent.
//			return new GarmonActionResult(getTicks(getNextNthOpponent(0, fighter)) + 1, action.getType(),
//					BigInteger.ZERO, null);
//
//		case SPECIAL_ATTACK:
//			var att = action.getSpecialAttack();
//			dmg = max(BigInteger.ZERO,
//					att.getPower().multiply(new BigDecimal(fighter.getAttack()))
//							.subtract(new BigDecimal(action.getTarget().getDefense())).setScale(0, RoundingMode.HALF_UP)
//							.toBigInteger());
//			action.getTarget().damage(dmg);
//			if (action.getTarget().isFainted())
//				remove(action.getTarget());
//			return new GarmonActionResult(att.getTicks(), action.getType(), dmg, action.getTarget());
//
//		default:
//			surrender(getTeam(fighter));
//			return new GarmonActionResult(100, action.getType(), BigInteger.ZERO, null);
//		}
//	}

}
