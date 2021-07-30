package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import gartham.c10ver.games.rpg.fighting.battles.api.Battle;

public class GarmonBattle extends Battle<GarmonBattleAction, GarmonFighter, GarmonTeam> {

	public GarmonTeam getFighterTeam(GarmonFighter fighter) {
		return getTeam(fighter);
	}

	private static final Random rand = new Random();

	public GarmonBattle(Collection<GarmonTeam> teams) {
		super(teams);
	}

	@SafeVarargs
	public GarmonBattle(GarmonTeam... teams) {
		super(teams);
	}

	private static final BigInteger max(BigInteger first, BigInteger second) {
		return first.compareTo(second) > 0 ? first : second;
	}

	public int getFighterTicks(GarmonFighter fighter) {
		return getTicks(fighter);
	}

	@Override
	protected int handleAction(GarmonBattleAction action, GarmonFighter fighter) {
		switch (action.getType()) {
		case ATTACK:
			BigInteger attack = fighter.getAttack();
			for (int i = 2; i < 13; i++)
				if (rand.nextInt(i) == 0)
					attack = attack.add(fighter.getAttack().divide(BigInteger.valueOf(i + 1)));
			action.getTarget().damage(max(BigInteger.ZERO, attack.subtract(action.getTarget().getDefense())));
			return 50;

		case SKIP_TURN:
			// Set ticks to be equal to the next OPPONENT in line + 1.
			// We need some way to get the next opponent.
			return getTicks(getNextNthOpponent(0, fighter)) + 1;

		case SPECIAL_ATTACK:
			var att = action.getSpecialAttack();
			action.getTarget()
					.damage(max(BigInteger.ZERO,
							att.getPower().multiply(new BigDecimal(fighter.getAttack()))
									.subtract(new BigDecimal(action.getTarget().getDefense()))
									.setScale(0, RoundingMode.HALF_UP).toBigInteger()));
			return att.getTicks();

		default:
			surrender(getTeam(fighter));
			return 100;
		}
	}

	/**
	 * Sorts the fighters according to their order in the battle queue.
	 * 
	 * @param fighters The list of {@link GarmonFighter}s to sort.
	 */
	public void sort(List<GarmonFighter> fighters) {
		Collections.sort(fighters, (o1, o2) -> {
			int c = sortingComparator().compare(o1, o2);
			return c == 0 ? Integer.compare(Collections.binarySearch(fighters, o1, sortingComparator()),
					Collections.binarySearch(fighters, o2, sortingComparator())) : c;
		});
	}

}
