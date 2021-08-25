package gartham.c10ver.games.rpg.creatures;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.util.Gateway;

import gartham.apps.garthchat.api.communication.common.gids.GID;
import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.games.rpg.fighting.fighters.CustomFighter;
import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public abstract class Creature extends PropertyObject implements Comparable<Creature> {

	private static final BigInteger BI_FIFTY = BigInteger.valueOf(50);
	private static final BigDecimal BI_TWO = BigDecimal.valueOf(2), C1 = BigDecimal.valueOf(5.48481),
			NATURAL_LOG_OF_TEN = BigDecimal.valueOf(2.302585092994);

	private static final BigDecimal ln(BigDecimal value, int itrc, int rprec) {
		if (value.signum() == 0)
			throw new ArithmeticException("Negative infinity.");
		int digits = value.precision() - value.scale() - 1;

		var shrunk = value.movePointLeft(digits);
		var frac = shrunk.subtract(BigDecimal.ONE).divide(shrunk.add(BigDecimal.ONE), RoundingMode.HALF_UP);

		BigDecimal bd = BigDecimal.ZERO;
		for (int i = 0; i < itrc; i++) {
			int q = 2 * i + 1;
			bd = bd.add(frac.pow(q).divide(BigDecimal.valueOf(q), RoundingMode.HALF_UP));
		}
		bd.setScale(rprec, RoundingMode.HALF_UP);

		return bd.multiply(BI_TWO).add(NATURAL_LOG_OF_TEN.multiply(BigDecimal.valueOf(digits)));
	}

	private static BigInteger xpForLevel(BigInteger level) {
		return C1.multiply(new BigDecimal(level)).multiply(ln(new BigDecimal(level), 5, 5))
				.setScale(0, RoundingMode.HALF_UP).toBigInteger().add(BI_FIFTY);
	}

	public BigInteger xpToNextLevel() {
		return xpForLevel(getLevel()).subtract(getXP());
	}

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
		case Nymph.TYPE -> new Nymph();

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

	public void setLevel(BigInteger level) {
		this.level.set(level);
	}

	public void setXP(BigInteger xp) {
		this.xp.set(xp);
	}

	/**
	 * Adds the specified amount of xp to this {@link Creature} and levels up the
	 * {@link Creature} as appropriate.
	 * 
	 * @param xp The amount of experience to add.
	 */
	public void rewardXP(BigInteger xp) {
		setXP(this.xp.get().add(xp));
		BigInteger xp2nl;
		while ((xp2nl = xpToNextLevel()).signum() < 1)
			setXP(xp2nl.negate());
	}

	public GID getID() {
		return id.get();
	}

	@Override
	public int compareTo(Creature o) {
		return getSpeed().compareTo(o.getSpeed());
	}

}
