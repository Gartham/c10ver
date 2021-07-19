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

	private static final BigInteger BI_THREE = BigInteger.valueOf(3);
	private static final BigDecimal BD_ONE_POINT_EIGHT = valueOf(1.8), BD_POINT_FIVE = valueOf(.5),
			BD_THREE = valueOf(3), BD_FOUR = valueOf(4);

	/**
	 * Factors representing how intrinsically strong this type of creature is in the
	 * respective stats.
	 */
	private final double hpf, attackf, speedf, deff;

	private static final double C1 = 2.52390554498, C2 = 1.40743710339, C3 = 0.6559414608, C4 = 0.21734580741;

	public static BigInteger evalstat(BigInteger level, double factor) {
		BigInteger lm2 = level.subtract(BigInteger.TWO);
		var u1 = new BigDecimal(lm2).divide(BD_THREE, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
		BigInteger lm1 = level.subtract(BigInteger.ONE);
		var u2 = new BigDecimal(lm1).divide(BD_THREE, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
		var u3 = new BigDecimal(level).divide(BD_THREE, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
		return new BigDecimal(level.subtract(BI_THREE)).divide(BD_FOUR, RoundingMode.FLOOR)
				.setScale(0, RoundingMode.FLOOR).multiply(valueOf(C1))
				.add(new BigDecimal(lm2).divide(BD_FOUR, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR)
						.multiply(valueOf(C2)))
				.add(new BigDecimal(lm1).divide(BD_FOUR, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR)
						.multiply(valueOf(C3)))
				.add(new BigDecimal(level)
						.divide(BD_FOUR, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR).multiply(valueOf(C4)))
				.add(valueOf(C1 + C2 + C3))
				.add(u1.multiply(u1.add(ONE)).add(u2.multiply(u2.add(ONE))).add(u3.multiply(u3.add(ONE)))
						.multiply(BD_POINT_FIVE))
				.add(new BigDecimal(level).multiply(BD_ONE_POINT_EIGHT)).setScale(0, RoundingMode.CEILING)
				.toBigInteger();
	}

	@Override
	public BigInteger getHp() {
		return evalstat(getLevel(), hpf);
	}

	@Override
	public BigInteger getAttack() {
		return evalstat(getLevel(), attackf);
	}

	@Override
	public BigInteger getSpeed() {
		return evalstat(getLevel(), speedf);
	}

	@Override
	public BigInteger getDefense() {
		return evalstat(getLevel(), deff);
	}

	public SimpleCreature(String type, double hpf, double attackf, double speedf, double deff) {
		super(type);
		this.hpf = hpf;
		this.attackf = attackf;
		this.speedf = speedf;
		this.deff = deff;
	}

	public SimpleCreature(String type, String fullImage, String pfp, String emoji, double hpf, double attackf,
			double speedf, double deff) {
		super(type, fullImage, pfp, emoji);
		this.hpf = hpf;
		this.attackf = attackf;
		this.speedf = speedf;
		this.deff = deff;
	}

	public SimpleCreature(JSONObject data, String expectedType, double hpf, double attackf, double speedf,
			double deff) {
		super(data, expectedType);
		this.hpf = hpf;
		this.attackf = attackf;
		this.speedf = speedf;
		this.deff = deff;
	}

}
