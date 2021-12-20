package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;
import java.util.Random;
import java.util.function.Consumer;

import gartham.c10ver.games.rpg.fighting.battles.api.Controller;
import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public class AIController<F extends GarmonFighter> implements Controller<F> {

	private static final Random RANDOM = new Random();

	private final GarmonBattle battle;
	private Consumer<Result> resultHandler;

	public Consumer<Result> getResultHandler() {
		return resultHandler;
	}

	public void setResultHandler(Consumer<Result> resultHandler) {
		this.resultHandler = resultHandler;
	}

	public AIController(GarmonBattle battle) {
		this.battle = battle;
	}

	protected AIController(GarmonBattle battle, Consumer<Result> resultHandler) {
		this.battle = battle;
		this.resultHandler = resultHandler;
	}

	public static class Result {
		private final Fighter target;
		private final BigInteger damageDealt;

		protected Result(Fighter target, BigInteger damageDealt) {
			this.target = target;
			this.damageDealt = damageDealt;
		}

		public Fighter getTarget() {
			return target;
		}

		public BigInteger getDamageDealt() {
			return damageDealt;
		}
	}

	@Override
	public int move(F fighter) {
		// Pick an enemy:
		var target = battle.pickRandomLivingOpponent(battle.getTeam(fighter));
		BigInteger attack = fighter.getAttack();
		for (int i = 2; i < 13; i++)
			if (RANDOM.nextInt(i) == 0)
				attack = attack.add(fighter.getAttack().divide(BigInteger.valueOf(i + 1)));
		BigInteger dmg = max(BigInteger.ZERO, attack.subtract(target.getDefense()));
		target.damage(dmg);
		resultHandler.accept(new Result(fighter, dmg));
		return 50;
	}

	private static BigInteger max(BigInteger first, BigInteger second) {
		return first.compareTo(second) > 0 ? first : second;
	}

}
