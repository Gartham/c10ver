package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public class GarmonAttack {
	private final BigInteger damage, defDrain, attackDrain;
	private final GarmonFighter target;

	public GarmonAttack(BigInteger damage, BigInteger defDrain, BigInteger attackDrain, GarmonFighter target) {
		this.damage = damage;
		this.defDrain = defDrain;
		this.attackDrain = attackDrain;
		this.target = target;
	}

	public GarmonAttack(BigInteger damage, GarmonFighter target) {
		this(damage, BigInteger.ZERO, BigInteger.ZERO, target);
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

	public GarmonFighter getTarget() {
		return target;
	}
}
