package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GarmonAttack {
	private final BigInteger defDrain, attackDrain;
	private final BigDecimal power;
	private final GarmonFighter target;

	public GarmonAttack(BigDecimal power, BigInteger defDrain, BigInteger attackDrain, GarmonFighter target) {
		this.power = power;
		this.defDrain = defDrain;
		this.attackDrain = attackDrain;
		this.target = target;
	}

	public GarmonAttack(BigDecimal power, GarmonFighter target) {
		this(power, BigInteger.ZERO, BigInteger.ZERO, target);
	}

	public BigDecimal getPower() {
		return power;
	}

	public BigInteger getDefDrain() {
		return defDrain;
	}

	public BigInteger getAttackDrain() {
		return attackDrain;
	}

	public GarmonFighter getTarget() {
		return target;
	}
}
