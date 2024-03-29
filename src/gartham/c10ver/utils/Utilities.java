package gartham.c10ver.utils;

import static gartham.c10ver.utils.Utilities.TimeUnit.DAYS;
import static gartham.c10ver.utils.Utilities.TimeUnit.HOURS;
import static gartham.c10ver.utils.Utilities.TimeUnit.MICROSECONDS;
import static gartham.c10ver.utils.Utilities.TimeUnit.MILLISECONDS;
import static gartham.c10ver.utils.Utilities.TimeUnit.MINUTES;
import static gartham.c10ver.utils.Utilities.TimeUnit.NANOSECONDS;
import static gartham.c10ver.utils.Utilities.TimeUnit.SECONDS;
import static gartham.c10ver.utils.Utilities.TimeUnit.YEARS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.streams.CharacterStream;
import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.economy.AbstractMultiplier;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.users.EconomyUser.Receipt;
import net.dv8tion.jda.api.interactions.components.Button;

public final class Utilities {

	public final static String CURRENCY_SYMBOL = "\u058D", ZERO_WIDTH_SPACE = "\u200B",
			UNICODE_EMOJI_ICON_VARIATION_SELECTOR = "\uFE0F";

	private static final MoneyUnit[] MONEY_UNITS = MoneyUnit.values();

	public enum TimeUnit {
		YEARS, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, MICROSECONDS, NANOSECONDS;
	}

	public enum RomanNumeral {
		I(1, false), IV(4, false), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);

		private final long value;
		private final boolean barrable;

		private RomanNumeral(long value) {
			this.value = value;
			barrable = true;
		}

		public StringBuilder bar(StringBuilder bar) {
			StringBuilder res = new StringBuilder();
			for (char c : name().toCharArray())
				res.append(c).append(bar);
			return res;
		}

		private RomanNumeral(long value, boolean barrable) {
			this.value = value;
			this.barrable = barrable;
		}

		public long getValue() {
			return value;
		}
	}

	public static double log2(double value) {
		return Math.log(value) / Math.log(2);
	}

	/**
	 * Performs a log operation that estimates the log of the provided
	 * {@link BigInteger} by an arbitrary base. The number is not exact because the
	 * result of the log operation in base 2 is floored before the change of base
	 * occurs.
	 * 
	 * @param base
	 * @param value
	 * @return
	 */
	public static BigDecimal logEstimation(long base, BigInteger value) {
		return BigDecimal.valueOf(((double) logBase2Floor(value)) / log2(base));
	}

	public static int logBase2Floor(BigInteger value) {
		return value.bitLength();
	}

	private static RomanNumeral[] ROMAN_NUMERALS = RomanNumeral.values();

	public static String toRomanNumerals(BigInteger number) {
		StringBuilder sb = new StringBuilder();
		if (number.compareTo(BigInteger.valueOf(4000)) >= 0) {
			StringBuilder bars = new StringBuilder("\u0305");
			BigInteger barc = BigInteger.valueOf(1000 * ROMAN_NUMERALS[ROMAN_NUMERALS.length - 1].value);
			while (number.compareTo(barc) > 0) {
				bars.append('\u0305');
				barc = barc.multiply(BigInteger.valueOf(1000));
			}
			while (!bars.isEmpty()) {
				barc = barc.divide(BigInteger.valueOf(1000));
				for (int i = ROMAN_NUMERALS.length - 1; i >= 1; i--) {
					BigInteger v = barc.multiply(BigInteger.valueOf(ROMAN_NUMERALS[i].value));
					while (v.compareTo(number) <= 0) {
						number = number.subtract(v);
						sb.append(ROMAN_NUMERALS[i].bar(bars));
					}
				}
				bars.deleteCharAt(bars.length() - 1);
			}
		}
		for (int i = ROMAN_NUMERALS.length - 1; i >= 0; i--) {
			BigInteger val = BigInteger.valueOf(ROMAN_NUMERALS[i].value);
			while (val.compareTo(number) <= 0) {
				number = number.subtract(val);
				sb.append(ROMAN_NUMERALS[i].name());
			}
		}
		return sb.toString();
	}

	public static String toRomanNumerals(long number) {
		return toRomanNumerals(BigInteger.valueOf(number));
	}

	public static JSONObject loadObj(File file) {
		return (JSONObject) load(file);
	}

	public static JSONValue load(File file) {
		if (!file.isFile())
			return null;
		try (var isr = new InputStreamReader(new FileInputStream(file))) {
			return new JSONParser().parse(CharacterStream.from(isr));
		} catch (Exception e) {
			throw new RuntimeException("Failed to load file: " + file, e);
		}
	}

	/**
	 * Parses the ID out of the mention stored in the given text, or returns
	 * <code>null</code> if the text is not a mention. The provided text must be
	 * exactly a mention.
	 * 
	 * @param mention The discord mention to parse. Maybe contain a <code>!</code>
	 *                character.
	 * @return The ID in the mention, as a {@link String}.
	 */
	public static String parseMention(String mention) {
		var m = Matching.build("<@").possibly("!").match(mention);
		if (m.length() == mention.length())
			return null;
		var nmo = Matching.numbers().match(m);
		if (nmo.length() == m.length())
			return null;
		var res = Matching.build(">").match(nmo);
		if (res.length() == nmo.length())
			return null;
		return m.substring(0, m.length() - nmo.length());
	}

	public static String parseChannelMention(String mention) {
		var m = Matching.build("<#").match(mention);
		if (m.length() == mention.length())
			return null;
		var nmo = Matching.numbers().match(m);
		if (nmo.length() == m.length())
			return null;
		var res = Matching.build(">").match(nmo);
		if (res.length() == nmo.length())
			return null;
		return m.substring(0, m.length() - nmo.length());
	}

	public static String parseRoleMention(String mention) {
		var m = Matching.build("<@&").match(mention);
		if (m.length() == mention.length())
			return null;
		var nmo = Matching.numbers().match(m);
		if (nmo.length() == m.length())
			return null;
		var res = Matching.build(">").match(nmo);
		if (res.length() == nmo.length())
			return null;
		return m.substring(0, m.length() - nmo.length());
	}

	public static void save(JSONValue obj, File file) {
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
			try (var pw = new PrintWriter(file)) {
				pw.println(obj.toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String formatLargest(Duration duration, int amt) {
		if (amt < 0)
			throw new IllegalArgumentException("Illegal amt: " + amt);
		else if (amt == 0)
			return "";
		var values = getParts(duration);

		List<TimeUnit> units = new ArrayList<>(amt);

		boolean f = false;
		for (TimeUnit tu : TimeUnit.values()) {
			if (amt == 0)
				break;
			if (f || values.get(tu) != 0) {
				f = true;
				units.add(tu);
				amt--;
			}
		}

		if (units.isEmpty())
			units.add(SECONDS);

		return format(values, units.toArray(new TimeUnit[units.size()]));

	}

	static Map<TimeUnit, Long> getParts(Duration duration) {
		int nanoseconds = duration.getNano();
		long seconds = duration.getSeconds();

		Map<TimeUnit, Long> values = new HashMap<>();

		values.put(NANOSECONDS, (long) (nanoseconds % 1000));
		values.put(MICROSECONDS, (long) ((nanoseconds /= 1000) % 1000));
		values.put(MILLISECONDS, (long) (nanoseconds / 1000));
		values.put(SECONDS, (long) (seconds % 60));
		values.put(MINUTES, (long) ((seconds /= 60) % 60));
		values.put(HOURS, (long) ((seconds /= 60) % 24));
		values.put(DAYS, (long) ((seconds /= 24) % 365));
		values.put(YEARS, (long) (seconds /= 365));

		return values;

	}

	static String format(Map<TimeUnit, Long> parts, TimeUnit... units) {
		final StringBuilder b = new StringBuilder();

		for (TimeUnit tu : units) {
			b.append(parts.get(tu));
			switch (tu) {
			case DAYS:
				b.append("d ");
				break;
			case HOURS:
				b.append("h ");
				break;
			case MICROSECONDS:
				b.append("\u00b5s ");
				break;
			case MILLISECONDS:
				b.append("ms ");
				break;
			case MINUTES:
				b.append("m ");
				break;
			case NANOSECONDS:
				b.append("ns ");
				break;
			case SECONDS:
				b.append("s ");
				break;
			case YEARS:
				b.append("y ");
				break;
			}
		}

		return b.toString().trim();
	}

	public static String format(Duration duration, TimeUnit... units) {
		return format(getParts(duration), units);
	}

	/**
	 * Formatting includes only all the time units that have non-zero value.
	 * 
	 * @param duration The duration to format.
	 * @return The formatted string.
	 */
	public static String formatRand(Duration duration) {
		var parts = getParts(duration);
		for (Iterator<Entry<TimeUnit, Long>> iterator = parts.entrySet().iterator(); iterator.hasNext();)
			if (iterator.next().getValue() == 0)
				iterator.remove();
		return format(duration, parts.keySet().toArray(new TimeUnit[parts.size()]));
	}

	public static String format(Duration duration) {
		var parts = getParts(duration);
		for (Iterator<Entry<TimeUnit, Long>> iterator = parts.entrySet().iterator(); iterator.hasNext();)
			if (iterator.next().getValue() == 0)
				iterator.remove();
		TimeUnit[] arr = parts.keySet().toArray(new TimeUnit[parts.size()]);
		Arrays.sort(arr);
		return format(duration, arr);
	}

	public static String listRewards(Receipt receipt) {
		StringBuilder sb = new StringBuilder();
		// List method impl
		if (!receipt.getRewards().getCloves().equals(BigInteger.ZERO))
			sb.append(format(receipt.getRewards().getCloves())).append('\n');
		for (var e : receipt.getRewards().getItems())
			for (var is : e)
				sb.append("`" + is.getCount() + "`x " + is.getItem().getIcon() + ' ' + is.getItem().getEffectiveName()
						+ '\n');

		sb.toString();
		for (var m : receipt.getRewards().getMults().entrySet())
			sb.append(m.getValue() + "x [**x").append(m.getKey().getAmt()).append("**] for ")
					.append(formatLargest(m.getKey().getDuration(), 2)).append('\n');
		// End List method impl
		sb.append("\nCloves Received: ").append(format(receipt.getRewards().getRewardedCloves()))
				.append(", New Balance: ").append(format(receipt.getTotalCloves()));
		var mul = multiplier(receipt.getRewards().getTotalMultiplier());
		if (mul != null)
			sb.append("\nTotal Mult: ").append("[**x").append(mul).append("**]");
		return sb.toString();
	}

	public static String list(BigInteger cloves, Inventory items, Map<AbstractMultiplier, Integer> mults) {
		StringBuilder sb = new StringBuilder();
		if (!cloves.equals(BigInteger.ZERO))
			sb.append(format(cloves)).append('\n');
		for (var e : items)
			for (var is : e)
				sb.append("`" + is.getCount() + "`x " + is.getItem().getIcon() + ' ' + is.getItem().getEffectiveName()
						+ '\n');
		sb.toString();
		for (var m : mults.entrySet())
			sb.append(m.getValue() + "x [**x").append(m.getKey().getAmt()).append("**] for ")
					.append(formatLargest(m.getKey().getDuration(), 2)).append('\n');
		return sb.toString();
	}

	public static String list(BigInteger cloves, Iterable<ItemBunch<?>> items, Map<AbstractMultiplier, Integer> mults) {
		StringBuilder sb = new StringBuilder();
		if (!cloves.equals(BigInteger.ZERO))
			sb.append(format(cloves)).append('\n');
		for (var is : items) {
			sb.append("`" + is.getCount() + "`x " + is.getItem().getIcon() + ' ' + is.getItem().getEffectiveName()
					+ '\n');
		}
		sb.toString();
		for (var m : mults.entrySet())
			sb.append(m.getValue() + "x [**x").append(m.getKey().getAmt()).append("**] for ")
					.append(formatLargest(m.getKey().getDuration(), 2)).append('\n');
		return sb.toString();
	}

	public static String multiplier(BigDecimal mult) {
		return multiplier(mult, 2);
	}

	public static String prettyPrintMultiplier(BigDecimal mult) {
		return mult == null ? null : "[**x" + multiplier(mult) + "**]";
	}

	public static String multiplier(BigDecimal mult, int scale) {
		return mult != null ? mult.setScale(scale, RoundingMode.HALF_UP).toPlainString() : null;
	}

	public enum MoneyUnit {
		GRAND("K"), MILLION("M"), BILLION("B"), TRILLION("T"), QUADRIILLION("Q");

		private final String symbol;
		private final BigInteger amt = BigInteger.valueOf(1000).pow(ordinal() + 1);

		public BigInteger getAmt() {
			return amt;
		}

		private MoneyUnit(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}
	}

	/**
	 * Formats the provided number as if it were a monetary value, (applying the
	 * {@link MoneyUnit} conversion, as appropriate), but does not prepend the
	 * {@link #CURRENCY_SYMBOL}.
	 * 
	 * @param number The {@link BigInteger} number to format.
	 * @return A string holding the formatted number.
	 */
	public static String formatNumber(BigInteger number) {
		MoneyUnit m = null;
		var bd = new BigDecimal(number);
		for (int i = 0; i < MONEY_UNITS.length; i++)
			if (number.compareTo(MONEY_UNITS[i].amt) >= 0)
				m = MONEY_UNITS[i];
			else
				break;
		if (m == null)
			return String.valueOf(number);

		var b = bd.divide(new BigDecimal(m.amt)).setScale(2, RoundingMode.HALF_UP);
		return b.stripTrailingZeros().toPlainString() + m.symbol;
	}

	public static String format(BigInteger money) {
		return CURRENCY_SYMBOL + ' ' + formatNumber(money);
	}

	public static String format(long money) {
		return format(BigInteger.valueOf(money));
	}

	public static double rand(double lower, double upper) {
		return lower + Math.random() * (upper - lower);
	}

	public static long rand(long lower, long upper) {
		return (long) rand(lower, upper + 1d);
	}

	public static int rand(int lower, int upper) {
		return (int) rand(lower, upper + 1d);
	}

	/**
	 * <p>
	 * Returns a random value whose distribution is centered at <code>mean</code>
	 * and whose distribution has a range of <code>2*deviation+1</code>. The
	 * distribution is uniform.
	 * </p>
	 * <p>
	 * This function returns a value that can be anywhere between
	 * <code>mean - deviation</code> and <code>mean + deviation</code>, uniformly at
	 * random (with each value having equal likelihood).
	 * </p>
	 * 
	 * @param mean      The center or average of the uniform distribution.
	 * @param deviation The deviation of the distribution.
	 * @return The randomly picked value.
	 */
	public static long randFromMean(long mean, long deviation) {
		return (long) (Math.random() * (2 * deviation + 1) + mean - deviation);
	}

	public static BigInteger randBIFromMean(long mean, long deviation) {
		return BigInteger.valueOf(randFromMean(mean, deviation));
	}

	public static String strip(String msg) {
		return stripEveryonePings(stripHerePings(msg));
	}

	public static String stripBackticks(String msg) {
		return msg.replace("`", "");
	}

	public static String stripEveryonePings(String msg) {
		return msg.replace("@everyone", "@\u200Beveryone");
	}

	public static String stripHerePings(String msg) {
		return msg.replace("@here", "@\u200Bhere");
	}

	public static Button copy(Button from, Button to) {
		to = from.isDisabled() ? to.asDisabled() : to.asEnabled();
		if (from.getEmoji() != null)
			to = to.withEmoji(from.getEmoji());
		if (from.getId() != null)
			to = to.withId(from.getId());
		if (from.getLabel() != null)
			to = to.withLabel(from.getLabel());
		if (from.getStyle() != null)
			to = to.withStyle(from.getStyle());
		if (from.getUrl() != null)
			to = to.withUrl(from.getUrl());
		return to;
	}

}
