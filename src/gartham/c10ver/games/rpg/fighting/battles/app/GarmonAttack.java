package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigDecimal;
import java.math.BigInteger;

public class GarmonAttack {
	private final BigInteger defDrain, attackDrain;
	private final BigDecimal power;
	private final GarmonFighter target;
	private final int ticks;

	public GarmonAttack(BigDecimal power, BigInteger defDrain, BigInteger attackDrain, GarmonFighter target,
			int ticks) {
		this.power = power;
		this.defDrain = defDrain;
		this.attackDrain = attackDrain;
		this.target = target;
		this.ticks = ticks;
	}

	public GarmonAttack(BigDecimal power, GarmonFighter target, int ticks) {
		this(power, BigInteger.ZERO, BigInteger.ZERO, target, ticks);
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

	public int getTicks() {
		return ticks;
	}
}
