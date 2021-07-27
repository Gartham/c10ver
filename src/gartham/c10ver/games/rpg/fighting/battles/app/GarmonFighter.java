package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;

import gartham.c10ver.games.rpg.creatures.Creature;
import gartham.c10ver.games.rpg.fighting.fighters.SimpleFighter;

public class GarmonFighter extends SimpleFighter {

	public GarmonFighter(BigInteger speed, BigInteger maxHealth, BigInteger health, BigInteger attack,
			BigInteger defense) {
		super(speed, maxHealth, health, attack, defense);
	}

	public GarmonFighter(Creature creature) {
		super(creature.getSpeed(), creature.getHp(), creature.getHp(), creature.getAttack(), creature.getDefense());
	}

	public void modDef(BigInteger amount) {
		setDefense(getDefense().add(amount));
	}

	public void modAtt(BigInteger amount) {
		setAttack(getAttack().add(amount));
	}

	public void modMaxHealth(BigInteger amount) {
		setMaxHealth(getMaxHealth().add(amount));
	}
}
