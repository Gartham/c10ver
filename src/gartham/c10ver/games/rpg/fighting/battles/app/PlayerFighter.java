package gartham.c10ver.games.rpg.fighting.battles.app;

import java.math.BigInteger;

public class PlayerFighter extends GarmonFighter {

	public PlayerFighter(String name, String headshot, BigInteger speed, BigInteger maxHealth, BigInteger health,
			BigInteger attack, BigInteger defense) {
		super(name, headshot, speed, maxHealth, health, attack, defense);
	}

	public PlayerFighter(String name, String emoji, String headshot, BigInteger speed, BigInteger maxHealth,
			BigInteger health, BigInteger attack, BigInteger defense) {
		super(name, emoji, headshot, speed, maxHealth, health, attack, defense);
	}

}
