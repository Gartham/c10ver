package gartham.c10ver.games.rpg.fighting.fighters;

import java.math.BigInteger;

public class CustomFighter extends SimpleFighter {
	private String fullImage, pfp, emoji, name;

	public String getFullImage() {
		return fullImage;
	}

	public String getPfp() {
		return pfp;
	}

	public String getEmoji() {
		return emoji;
	}

	public String getName() {
		return name;
	}

	public CustomFighter(BigInteger speed, BigInteger maxHealth, BigInteger health, BigInteger attack,
			BigInteger defense, String fullImage, String pfp, String emoji, String name) {
		this.speed = speed;
		this.maxHealth = maxHealth;
		this.health = health;
		this.attack = attack;
		this.defense = defense;
		this.fullImage = fullImage;
		this.pfp = pfp;
		this.emoji = emoji;
		this.name = name;
	}

	public CustomFighter(BigInteger speed, BigInteger maxHealth, BigInteger attack, BigInteger defense,
			String fullImage, String pfp, String emoji, String name) {
		this(speed, maxHealth, maxHealth, attack, defense, fullImage, pfp, emoji, name);
	}

	public Fighter setMaxHealth(BigInteger maxHealth) {
		this.maxHealth = maxHealth;
		return this;
	}

	public Fighter setHealth(BigInteger health) {
		this.health = health;
		return this;
	}

	public Fighter setAttack(BigInteger attack) {
		this.attack = attack;
		return this;
	}

	public Fighter setDefense(BigInteger defense) {
		this.defense = defense;
		return this;
	}

	public Fighter setFullImage(String fullImage) {
		this.fullImage = fullImage;
		return this;
	}

	public Fighter setPfp(String pfp) {
		this.pfp = pfp;
		return this;
	}

	public Fighter setEmoji(String emoji) {
		this.emoji = emoji;
		return this;
	}

	public Fighter setName(String name) {
		this.name = name;
		return this;
	}
}
