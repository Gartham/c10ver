package gartham.c10ver.games.rpg.creatures;

import java.math.BigInteger;

import gartham.c10ver.data.PropertyObject;

public class Creature extends PropertyObject implements Comparable<Creature> {

	private final Property<Integer> hp = intProperty("hp"), attack = intProperty("attack"),
			speed = intProperty("speed"), defense = intProperty("defense"), level = intProperty("level", 1);
	private final Property<BigInteger> xp = bigIntegerProperty("xp");

	public Property<Integer> getHPProperty() {
		return hp;
	}

	public Property<Integer> getAttackProperty() {
		return attack;
	}

	public Property<Integer> getSpeedProperty() {
		return speed;
	}

	public Property<Integer> getDefenseProperty() {
		return defense;
	}

	public Property<Integer> getLevelProperty() {
		return level;
	}

	public Property<BigInteger> getXPProperty() {
		return xp;
	}

	public int getHp() {
		return hp.get();
	}

	public int getAttack() {
		return attack.get();
	}

	public int getSpeed() {
		return speed.get();
	}

	public int getDefense() {
		return defense.get();
	}

	public int getLevel() {
		return level.get();
	}

	public BigInteger getXP() {
		return xp.get();
	}

	@Override
	public int compareTo(Creature o) {
		return getSpeed() - o.getSpeed();
	}

}
