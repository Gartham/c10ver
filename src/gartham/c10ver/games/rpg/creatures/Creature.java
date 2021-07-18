package gartham.c10ver.games.rpg.creatures;

import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.util.Gateway;

import gartham.apps.garthchat.api.communication.common.gids.GID;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.games.rpg.fighting.Fighter;
import gartham.c10ver.games.rpg.fighting.FighterController;

public abstract class Creature extends PropertyObject implements Comparable<Creature> {

	private final Property<String> fullImage = stringProperty("full-image"), pfp = stringProperty("pfp"),
			type = stringProperty("type");
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

	public String getType() {
		return type.get();
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

	protected Creature(String type) {
		id.set(GID.newGID());
		this.type.set(type);
	}

	protected Creature(String type, String fullImage, String pfp) {
		id.set(GID.newGID());
		this.type.set(type);
	}

	protected Creature setPFP(String pfp) {
		this.pfp.set(pfp);
		return this;
	}

	protected Creature setFullImage(String fullImage) {
		this.fullImage.set(fullImage);
		return this;
	}

	public static Creature from(JSONObject json) {
		switch (json.getString("type")) {
		// TODO Add creatures.
		default:
			throw new IllegalArgumentException("Unexpected value: " + ((JSONObject) json).getString("type"));
		}
	}

	protected Creature(JSONObject json, String type) {
		if (!json.getString("type").equals(type))
			throw new IllegalArgumentException("Invalid type for a creature data file (stored: " + json.getString(type)
					+ ", expected: " + type + ").");
		load(json);
	}

	public abstract int getHp();

	public abstract int getAttack();

	public abstract int getSpeed();

	public abstract int getDefense();

	public abstract int getLevel();

	public abstract BigInteger getXP();

	public GID getID() {
		return id.get();
	}

	@Override
	public int compareTo(Creature o) {
		return getSpeed() - o.getSpeed();
	}

}
