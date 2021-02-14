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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.streams.CharacterStream;

import gartham.c10ver.economy.items.ItemBunch;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public final class Utilities {

	private final static String CURRENCY_SYMBOL = "\u058D";

	private static final MoneyUnit[] MONEY_UNITS = MoneyUnit.values();

	public enum TimeUnit {
		YEARS, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, MICROSECONDS, NANOSECONDS;
	}

	public static JSONObject loadObj(File file) {
		return (JSONObject) load(file);
	}

	public static JSONValue load(File file) {
		if (!file.isFile())
			return null;
		try (var isr = new InputStreamReader(new FileInputStream(file))) {
			return new JSONParser().parse(CharacterStream.from(isr));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		int nanoseconds = duration.getNano();
		long seconds = duration.getSeconds();

		final StringBuilder b = new StringBuilder();
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

		if (units.isEmpty()) {
			units.add(SECONDS);
		}

		return format(values, units.toArray(new TimeUnit[units.size()]));

	}

	static Map<TimeUnit, Long> getParts(Duration duration) {
		int nanoseconds = duration.getNano();
		long seconds = duration.getSeconds();

		Map<TimeUnit, Long> values = new HashMap<>();

		values.put(NANOSECONDS, (long) (nanoseconds % 1000));
		values.put(MICROSECONDS, (long) ((nanoseconds /= 1000) % 1000));
		values.put(MILLISECONDS, (long) (nanoseconds / 1000));
		values.put(SECONDS, (long) (int) (seconds % 60));
		values.put(MINUTES, (long) (int) ((seconds /= 60) % 60));
		values.put(HOURS, (long) (int) ((seconds /= 60) % 24));
		values.put(DAYS, (long) (int) ((seconds /= 24) % 365));
		values.put(YEARS, (long) (int) (seconds /= 365));

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
		int nanoseconds = duration.getNano();
		long seconds = duration.getSeconds();

		Map<TimeUnit, Long> parts = getParts(duration);
		return format(parts, units);
	}

	public static <E> List<E> paginate(int page, int itemsPerPage, List<E> items) {
		if (items.size() == 0 && page == 1)
			return items.subList(0, 0);
		int item = (page - 1) * itemsPerPage;
		int maxPage = maxPage(itemsPerPage, items);
		if (page < 1 || page > maxPage)
			return null;

		return items.subList(item, Math.min(item + itemsPerPage, items.size()));
	}

	public static int maxPage(int itemsPerPage, List<?> items) {
		return maxPage(itemsPerPage, items.size());
	}

	public static int maxPage(int itemsPerPage, int listSize) {
		return (listSize + itemsPerPage - 1) / itemsPerPage;
	}

	public static String listRewards(long cloves, ItemBunch<?>... items) {
		return listRewards(BigInteger.valueOf(cloves), items);
	}

	public static String listRewards(long cloves, Iterable<ItemBunch<?>> items) {
		return listRewards(BigInteger.valueOf(cloves), items);
	}

	public static String listRewards(BigInteger cloves, ItemBunch<?>... items) {
		return listRewards(cloves, null, items);
	}

	public static String listRewards(BigInteger cloves, Iterable<ItemBunch<?>> items) {
		return listRewards(cloves, null, items);
	}

	public static String listRewards(BigInteger cloves, BigDecimal mult, ItemBunch<?>... items) {
		StringBuilder sb = new StringBuilder();
		if (!cloves.equals(BigInteger.ZERO))
			sb.append(format(cloves) + "\n");
		for (ItemBunch<?> ib : items)
			sb.append("`" + ib.getCount() + "`x" + ib.getItem().getIcon() + ' ' + ib.getItem().getCustomName() + '\n');
		if (mult != null) {
			var scaled = mult.setScale(2, RoundingMode.HALF_UP);
			if (scaled.compareTo(BigDecimal.TEN) != 0)
				sb.append("\nMultiplier: [**x" + scaled.toPlainString() + "**]");
		}
		return sb.toString();
	}

	public static String listRewards(BigInteger cloves, BigDecimal mult, Iterable<ItemBunch<?>> items) {
		StringBuilder sb = new StringBuilder();
		if (!cloves.equals(BigInteger.ZERO))
			sb.append(format(cloves) + "\n");
		for (ItemBunch<?> ib : items)
			sb.append("`" + ib.getCount() + "`x" + ib.getItem().getIcon() + ' ' + ib.getItem().getCustomName() + '\n');
		if (mult != null) {
			var scaled = mult.setScale(2, RoundingMode.HALF_UP);
			if (scaled.compareTo(BigDecimal.TEN) != 0)
				sb.append("\nMultiplier: [**x" + scaled.toPlainString() + "**]");
		}
		return sb.toString();
	}

	public enum MoneyUnit {
		GRAND("K"), MILLION("M"), BILLION("B"), TRILLION("T");

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

	public static String format(BigInteger money) {
		MoneyUnit m = null;
		var bd = new BigDecimal(money);
		for (int i = 0; i < MONEY_UNITS.length; i++)
			if (money.compareTo(MONEY_UNITS[i].amt) >= 0)
				m = MONEY_UNITS[i];
			else
				break;
		if (m == null)
			return CURRENCY_SYMBOL + ' ' + String.valueOf(money);

		var b = bd.divide(new BigDecimal(m.amt)).setScale(2, RoundingMode.HALF_UP);
		return CURRENCY_SYMBOL + ' ' + b.stripTrailingZeros().toPlainString() + m.symbol;
	}

}
