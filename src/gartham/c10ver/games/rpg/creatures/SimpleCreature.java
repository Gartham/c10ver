package gartham.c10ver.games.rpg.creatures;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import static java.math.BigDecimal.*;

import org.alixia.javalibrary.json.JSONObject;

/**
 * @author Gartham
 *
 */
public class SimpleCreature extends Creature {

	/**
	 * Factors representing how intrinsically strong this type of creature is in the
	 * respective stats.
	 */
	private final double hpf, attackf, speedf, deff;

	private static final double C1 = 1152 * 2.44948974278 / 1118.03398875, C2 = 361 * 4.35889894354 / 1118.03398875,
			C3 = 196 * 3.74165738677 / 1118.03398875, C4 = 243 / 1118.03398875;

	private static BigInteger evalstat(BigInteger level, double factor) {
		var t1 = new BigDecimal(level.subtract(BigInteger.valueOf(3))).divide(valueOf(4), RoundingMode.FLOOR)
				.setScale(0, RoundingMode.FLOOR).multiply(valueOf(C1));
		var t2 = new BigDecimal(level.subtract(BigInteger.TWO)).divide(valueOf(4), RoundingMode.FLOOR)
				.setScale(0, RoundingMode.FLOOR).multiply(valueOf(C2));
		var t3 = new BigDecimal(level.subtract(BigInteger.ONE)).divide(valueOf(4), RoundingMode.FLOOR)
				.setScale(0, RoundingMode.FLOOR).multiply(valueOf(C3));
		var t4 = new BigDecimal(level).divide(valueOf(4), RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR)
				.multiply(valueOf(C4));
		var t5 = valueOf(C1 + C2 + C3);
		var tmerge = t1.add(t2).add(t3).add(t4).add(t5);

		var u1 = new BigDecimal(level.subtract(BigInteger.TWO)).divide(valueOf(3), RoundingMode.FLOOR).setScale(0,
				RoundingMode.FLOOR);
		var u2 = new BigDecimal(level.subtract(BigInteger.ONE)).divide(valueOf(3), RoundingMode.FLOOR).setScale(0,
				RoundingMode.FLOOR);
		var u3 = new BigDecimal(level).divide(valueOf(3), RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
		var umerge = u1.multiply(u1.add(ONE)).add(u2.multiply(u2.add(ONE))).add(u3.multiply(u3.add(ONE)))
				.multiply(valueOf(.5));
		var v = new BigDecimal(level).multiply(valueOf(1.8));
		return tmerge.add(umerge).add(v).setScale(0, RoundingMode.CEILING).toBigInteger();
	}

	@Override
	public BigInteger getHp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger getAttack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger getSpeed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigInteger getDefense() {
		// TODO Auto-generated method stub
		return null;
	}

}
