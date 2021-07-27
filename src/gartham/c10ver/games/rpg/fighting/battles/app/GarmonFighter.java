package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;

import gartham.c10ver.games.rpg.fighting.fighters.SimpleFighter;

public class GarmonFighter extends SimpleFighter {
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
