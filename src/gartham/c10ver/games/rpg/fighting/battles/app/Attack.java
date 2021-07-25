package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;

public class Attack {
	private final BigInteger damage, defDrain, attackDrain;

	public Attack(BigInteger damage, BigInteger defDrain, BigInteger attackDrain) {
		this.damage = damage;
		this.defDrain = defDrain;
		this.attackDrain = attackDrain;
	}

	public Attack(BigInteger damage) {
		this(damage, BigInteger.ZERO, BigInteger.ZERO);
	}

	public BigInteger getDamage() {
		return damage;
	}

	public BigInteger getDefDrain() {
		return defDrain;
	}

	public BigInteger getAttackDrain() {
		return attackDrain;
	}
}
