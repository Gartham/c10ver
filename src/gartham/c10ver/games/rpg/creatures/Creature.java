package gartham.c10ver.games.rpg.creatures;

import java.math.BigInteger;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.util.Gateway;

import gartham.apps.garthchat.api.communication.common.gids.GID;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.games.rpg.fighting.fighters.Fighter;
import gartham.c10ver.games.rpg.fighting.fighters.CustomFighter;

public abstract class Creature extends PropertyObject implements Comparable<Creature> {

	protected String fullImage, pfp, emoji, name;
	private final Property<String> type = stringProperty("type");
	private final Property<BigInteger> xp = bigIntegerProperty("xp"),
			level = bigIntegerProperty("level", BigInteger.ONE);
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

	public String getName() {
		return name;
	}

	public String getType() {
		return type.get();
	}

	public String getPFP() {
		return pfp;
	}

	public String getFullImage() {
		return fullImage;
	}

	public String getEmoji() {
		return emoji;
	}

	public Fighter makeFighter() {
		return new CustomFighter(getSpeed(), getHp(), getAttack(), getDefense(), getFullImage(), getPFP(), getEmoji(),
				getName());
	}

	protected Creature(String type) {
		id.set(GID.newGID());
		this.type.set(type);
		level.set(BigInteger.ONE);
	}

	protected Creature(String type, String fullImage, String pfp, String emoji, String name) {
		id.set(GID.newGID());
		this.type.set(type);
		setPFP(pfp).setFullImage(fullImage).setEmoji(emoji);
		level.set(BigInteger.ONE);
		this.name = name;

	}

	protected Creature setPFP(String pfp) {
		this.pfp = pfp;
		return this;
	}

	protected Creature setFullImage(String fullImage) {
		this.fullImage = fullImage;
		return this;
	}

	protected Creature setEmoji(String emoji) {
		this.emoji = emoji;
		return this;
	}

	protected Creature setName(String name) {
		this.name = name;
		return this;
	}

	public static Creature from(JSONObject json) {
		return switch (json.getString("type")) {
		case NymphCreature.TYPE -> new NymphCreature();

		default -> throw new IllegalArgumentException("Unexpected value: " + json.getString("type"));
		};
	}

	protected Creature(JSONObject data, String expectedType) {
		if (!data.getString("type").equals(expectedType))
			throw new IllegalArgumentException("Invalid type for a creature data file (stored: "
					+ data.getString(expectedType) + ", expected: " + expectedType + ").");
		load(data);
	}

	public abstract BigInteger getHp();

	public abstract BigInteger getAttack();

	public abstract BigInteger getSpeed();

	public abstract BigInteger getDefense();

	public final BigInteger getLevel() {
		return level.get();
	}

	public final BigInteger getXP() {
		return xp.get();
	}

	public GID getID() {
		return id.get();
	}

	@Override
	public int compareTo(Creature o) {
		return getSpeed().compareTo(o.getSpeed());
	}

}
