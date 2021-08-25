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
		super(speed, maxHealth, health, attack, defense);
		this.fullImage = fullImage;
		this.pfp = pfp;
		this.emoji = emoji;
		this.name = name;
	}

	public CustomFighter(BigInteger speed, BigInteger maxHealth, BigInteger attack, BigInteger defense,
			String fullImage, String pfp, String emoji, String name) {
		this(speed, maxHealth, maxHealth, attack, defense, fullImage, pfp, emoji, name);
	}

	public void setMaxHealth(BigInteger maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setHealth(BigInteger health) {
		this.health = health;
	}

	public void setAttack(BigInteger attack) {
		this.attack = attack;
	}

	public void setDefense(BigInteger defense) {
		this.defense = defense;
	}

	public void setFullImage(String fullImage) {
		this.fullImage = fullImage;
	}

	public void setPfp(String pfp) {
		this.pfp = pfp;
	}

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

	public void setName(String name) {
		this.name = name;
	}
}
