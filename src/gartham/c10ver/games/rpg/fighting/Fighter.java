package gartham.c10ver.games.rpg.fighting;

public class Fighter implements Comparable<Fighter> {
	private int speed, maxHealth, health, attack, defense;
	private String fullImage, pfp;
	private FighterController controller;

	public FighterController getController() {
		return controller;
	}

	/**
	 * Sets this {@link Fighter}'s controller.
	 * 
	 * @param controller The new controller.
	 * @return Returns this object for chaining.
	 */
	public Fighter setController(FighterController controller) {
		this.controller = controller;
		return this;
	}

	public int getSpeed() {
		return speed;
	}

	public String getFullImage() {
		return fullImage;
	}

	public String getPfp() {
		return pfp;
	}

	public Fighter(int speed, int maxHealth, int health, int attack, int defense, String fullImage, String pfp,
			FighterController controller) {
		this.speed = speed;
		this.maxHealth = maxHealth;
		this.health = health;
		this.attack = attack;
		this.defense = defense;
		this.fullImage = fullImage;
		this.pfp = pfp;
		this.controller = controller;
	}

	public Fighter(int speed, int maxHealth, int attack, int defense, String fullImage, String pfp,
			FighterController controller) {
		this(speed, maxHealth, maxHealth, attack, defense, fullImage, pfp, controller);
	}

	public Fighter(int speed, int maxHealth, int health, int attack, int defense, String fullImage, String pfp) {
		this(speed, maxHealth, health, attack, defense, fullImage, pfp, null);
	}

	public Fighter(int speed, int maxHealth, int attack, int defense, String fullImage, String pfp) {
		this(speed, maxHealth, maxHealth, attack, defense, fullImage, pfp);
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getHealth() {
		return health;
	}

	public int getAttack() {
		return attack;
	}

	public int getDefense() {
		return defense;
	}

	public void heal(int amount) {
		if (amount == 0)
			return;
		else if (amount < 0)
			damage(-amount);
		else {
			health += amount;
			if (health >= maxHealth)
				health = maxHealth;
		}
	}

	public boolean isFainted() {
		return health == 0;
	}

	/**
	 * Damages this fighter by the specified amount and returns <code>true</code> if
	 * the fighter fainted (i.e. this fighter's health dropped below 1) as a result.
	 * 
	 * @param amount The amount of damage to do. If zero, this method returns
	 *               {@link #isFainted()}. If negative, calls
	 *               <code>{@link #heal(int) heal(-amount)}</code> and then returns
	 *               <code>false</code>.
	 * @return If this fighter is fainted after processing of this method.
	 */
	public boolean damage(int amount) {
		if (amount == 0)
			return isFainted();
		else if (amount < 0) {
			heal(-amount);
			return false;
		}
		if ((health -= amount) < 0) {
			health = 0;
			return true;
		}
		return health == 0;
	}

	public String getHealthString() {
		return health == maxHealth ? "FULL" : isFainted() ? "FAINTED" : health + " / " + maxHealth;
	}

	@Override
	public final int compareTo(Fighter o) {
		return speed - o.speed;
	}
}
