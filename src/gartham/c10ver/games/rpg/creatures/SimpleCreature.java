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

	private static final BigDecimal BC1 = valueOf(2.52390554498), BC2 = valueOf(1.40743710339),
			BC3 = valueOf(0.6559414608), BC4 = valueOf(0.21734580741), BC5 = valueOf(4.58728410917);

	public static BigInteger evalstat(BigInteger level, double factor) {
		BigInteger lm2 = level.subtract(BigInteger.TWO);
		var u1 = new BigDecimal(lm2).divide(BD_THREE, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
		BigInteger lm1 = level.subtract(BigInteger.ONE);
		var u2 = new BigDecimal(lm1).divide(BD_THREE, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
		var u3 = new BigDecimal(level).divide(BD_THREE, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
		return new BigDecimal(level.subtract(BI_THREE)).divide(BD_FOUR, RoundingMode.FLOOR)
				.setScale(0, RoundingMode.FLOOR).multiply(BC1)
				.add(new BigDecimal(lm2).divide(BD_FOUR, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR)
						.multiply(BC2))
				.add(new BigDecimal(lm1).divide(BD_FOUR, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR)
						.multiply(BC3))
				.add(new BigDecimal(level).divide(BD_FOUR, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR)
						.multiply(BC4))
				.add(BC5)
				.add(u1.multiply(u1.add(ONE)).add(u2.multiply(u2.add(ONE))).add(u3.multiply(u3.add(ONE)))
						.multiply(BD_POINT_FIVE))
				.add(new BigDecimal(level).multiply(BD_ONE_POINT_EIGHT)).multiply(valueOf(factor))
				.setScale(0, RoundingMode.CEILING).toBigInteger();
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

	public SimpleCreature(String type, String fullImage, String pfp, String emoji, String name, double hpf,
			double attackf, double speedf, double deff) {
		super(type, fullImage, pfp, emoji, name);
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
