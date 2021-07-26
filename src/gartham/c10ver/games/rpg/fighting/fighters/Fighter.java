package gartham.c10ver.games.rpg.fighting.fighters;

import java.math.BigInteger;

public class Fighter implements Comparable<Fighter> {
	private BigInteger speed, maxHealth, health, attack, defense;
	private String fullImage, pfp, emoji, name;

	public BigInteger getSpeed() {
		return speed;
	}

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

	public Fighter(BigInteger speed, BigInteger maxHealth, BigInteger health, BigInteger attack, BigInteger defense,
			String fullImage, String pfp, String emoji, String name) {
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

	public Fighter(BigInteger speed, BigInteger maxHealth, BigInteger attack, BigInteger defense, String fullImage,
			String pfp, String emoji, String name) {
		this(speed, maxHealth, maxHealth, attack, defense, fullImage, pfp, emoji, name);
	}

	public BigInteger getMaxHealth() {
		return maxHealth;
	}

	public BigInteger getHealth() {
		return health;
	}

	public BigInteger getAttack() {
		return attack;
	}

	public BigInteger getDefense() {
		return defense;
	}

	public void heal(BigInteger amount) {
		if (amount.equals(BigInteger.ZERO))
			return;
		else if (amount.compareTo(BigInteger.ZERO) < 0)
			damage(amount.negate());
		else {
			health = health.add(amount);
			if (health.compareTo(maxHealth) >= 0)
				health = maxHealth;
		}
	}

	public void heal(long amount) {
		heal(BigInteger.valueOf(amount));
	}

	public boolean isFainted() {
		return health.equals(BigInteger.ZERO);
	}

	public boolean damage(long amount) {
		return damage(BigInteger.valueOf(amount));
	}

	/**
	 * Damages this fighter by the specified amount and returns <code>true</code> if
	 * the fighter fainted (i.e. this fighter's health dropped below 1) as a result.
	 * 
	 * @param amount The amount of damage to do. If zero, this method returns
	 *               {@link #isFainted()}. If negative, calls
	 *               <code>{@link #heal(BigInteger) heal(-amount)}</code> and then
	 *               returns <code>false</code>.
	 * @return If this fighter is fainted after processing of this method.
	 */
	public boolean damage(BigInteger amount) {
		if (amount.equals(BigInteger.ZERO))
			return isFainted();
		else if (amount.compareTo(BigInteger.ZERO) < 0) {
			heal(amount.negate());
			return false;
		}
		if ((health = health.subtract(amount)).compareTo(BigInteger.ZERO) < 0) {
			health = BigInteger.ZERO;
			return true;
		}
		return health.equals(BigInteger.ZERO);
	}

	public String getHealthString() {
		return health == maxHealth ? "FULL" : isFainted() ? "FAINTED" : health + " / " + maxHealth;
	}

	@Override
	public final int compareTo(Fighter o) {
		return speed.compareTo(o.speed);
	}

	public Fighter setSpeed(BigInteger speed) {
		this.speed = speed;
		return this;
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
