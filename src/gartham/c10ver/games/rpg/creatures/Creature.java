package gartham.c10ver.games.rpg.creatures;

import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.util.Gateway;

import gartham.apps.garthchat.api.communication.common.gids.GID;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.games.rpg.fighting.Fighter;
import gartham.c10ver.games.rpg.fighting.FighterController;

public class Creature extends PropertyObject implements Comparable<Creature> {

	private final Property<Integer> hp = intProperty("hp"), attack = intProperty("attack"),
			speed = intProperty("speed"), defense = intProperty("defense"), level = intProperty("level", 1);
	private final Property<BigInteger> xp = bigIntegerProperty("xp");

	private final Property<String> fullImage = stringProperty("full-image"), pfp = stringProperty("pfp");
	private final Property<GID> id = toStringProperty("id", new Gateway<>() {

		@Override
		public GID to(String value) {
			return GID.fromHex(value);
		}

		@Override
		public String from(GID value) {
			return value.getHex();
		}
	});

	public void setHP(int hp) {
		this.hp.set(hp);
	}

	public void setAttack(int attack) {
		this.attack.set(attack);
	}

	public void setSpeed(int speed) {
		this.speed.set(speed);
	}

	public void setDefense(int def) {
		defense.set(def);
	}

	public void setLevel(int level) {
		this.level.set(level);
	}

	public void setXP(BigInteger xp) {
		this.xp.set(xp);
	}

	public void setFullImage(String imageURL) {
		fullImage.set(imageURL);
	}

	public void setPFP(String url) {
		pfp.set(url);
	}

	public String getFullImage() {
		return fullImage.get();
	}

	public String getPFP() {
		return pfp.get();
	}

	public Fighter makeFighter() {
		return new Fighter(getSpeed(), getHp(), getAttack(), getDefense(), getFullImage(), getPFP());
	}

	public Fighter makeFighter(FighterController controller) {
		return new Fighter(getSpeed(), getHp(), getAttack(), getDefense(), getFullImage(), getPFP(), controller);
	}

	public Creature() {
		id.set(GID.newGID());
	}

	public Creature(JSONObject json) {
		load(json);
	}

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

	public GID getID() {
		return id.get();
	}

	@Override
	public int compareTo(Creature o) {
		return getSpeed() - o.getSpeed();
	}

}
