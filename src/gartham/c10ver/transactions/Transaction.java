package gartham.c10ver.transactions;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONNumber;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.json.JSONString;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.streams.CharacterStream;

import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.utility.multickets.MultiplierTicket;

public class Transaction {

	public static final class Entry {
		private final Item item;
		private final BigInteger amt;

		public Entry(Item item, BigInteger amt) {
			this.item = item;
			this.amt = amt;
		}

		public BigInteger getAmt() {
			return amt;
		}

		public Item getItem() {
			return item;
		}

	}

	public static long determinePrice(String itemName) {
		return switch (itemName) {
		case "W-1" -> 99;
		case "W-2" -> 199;
		case "W-3" -> 299;

		case "R-1" -> 499;
		case "R-2" -> 899;
		case "R-3" -> 1399;
		case "R-4" -> 1899;

		case "HP-1" -> 1299;
		case "HP-2" -> 2199;

		case "G-1" -> 4999;

		default -> throw new IllegalArgumentException("Unexpected value: " + itemName);
		};
	}

	public static String determineColor(String itemName) {
		return switch (itemName.substring(0, itemName.indexOf('-'))) {
		case "W" -> "<:WhiteTicket:844063172247158824>";
		case "R" -> "<:RedTicket:844063172196433951>";
		case "HP" -> "<:HotPinkTicket:844063172024860723>";
		case "G" -> "<:GoldTicket:844063172260003840>";

		default -> throw new IllegalArgumentException(
				"Unexpected value: " + itemName.substring(0, itemName.indexOf('-')));
		};
	}

	public static BigDecimal determineAmount(String itemName) {
		return switch (itemName.substring(0, itemName.indexOf('-'))) {
		case "W" -> BigDecimal.valueOf(25, 2);
		case "R" -> BigDecimal.valueOf(1);
		case "HP" -> BigDecimal.valueOf(225, 2);
		case "G" -> BigDecimal.valueOf(5);

		default -> throw new IllegalArgumentException("Unexpected value: " + determineColor(itemName));
		};
	}

	public static Duration determineDuration(String numb) {
		return Duration.ofSeconds(60 * switch (numb) {
		case "W-1", "R-1" -> 15;
		case "W-2", "R-2", "HP-1" -> 30;
		case "W-3", "R-3", "HP-2", "G-1" -> 60;
		case "R-4" -> 120;

		default -> throw new IllegalArgumentException("Unexpected value: " + numb);
		});
	}

	private final List<Entry> items;
	private final String userID;

	public List<Entry> getItems() {
		return items;
	}

	public String getUserID() {
		return userID;
	}

	public Transaction(List<Entry> items, String userID) {
		this.items = items;
		this.userID = userID;
	}

	public static boolean verifyPaypalTransaction(JSONObject json) {
		BigDecimal total = new BigDecimal(json.getString("mc_gross"));
		int itemNumb = Integer.valueOf(json.getString("num_cart_items"));

		long tot = 0;

		for (int i = 1; i <= itemNumb; i++) {
			String iname = json.getString("item_name" + i);
			int quantity = Integer.valueOf(json.getString("quantity" + i));
			long price = determinePrice(iname);

			double tt = Math.floor(1.0825 * price) * quantity;
			tot += tt;
		}

		BigDecimal amt = BigDecimal.valueOf(tot).divide(BigDecimal.valueOf(100));
		if (total.compareTo(amt) != 0) {
			StringBuilder sb = new StringBuilder().append("Discrepancy in purchase by user: ")
					.append(json.getString("custom")).append(".\nPaypal Purchase Amount: ")
					.append(total.toPlainString()).append("\nCalculated Value of Purchase: ")
					.append(amt.toPlainString()).append("\nTransaction Info:\n").append(json).append("\n\n");
			System.err.println(sb);
			try (PrintWriter pw = new PrintWriter("PaymentValidatorLog.txt")) {
				pw.println(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		} else
			return true;
	}

	public static Transaction fromPaypalJSON(JSONValue json) {
		JSONObject obj = (JSONObject) json;
		if (!verifyPaypalTransaction(obj))
			throw new RuntimeException("Illegal Transaction");
		String userid = obj.getString("custom");
		int itemNumb = Integer.valueOf(obj.getString("num_cart_items"));
		List<Entry> entries = new ArrayList<>();
		for (int i = 1; i <= itemNumb; i++) {
			try {
				String iname = obj.getString("item_name" + i);
				int itemCount = Integer.valueOf(obj.getString("quantity" + i));
				var color = determineColor(iname);
				var amt = determineAmount(iname);
				var dur = determineDuration(iname);

				entries.add(new Entry(new MultiplierTicket(color, amt, dur), BigInteger.valueOf(itemCount)));
			} catch (Exception e) {
				System.out.println("Failed to give rewards for " + i);
				System.err.println(obj);
				e.printStackTrace();
			}
		}

		return new Transaction(entries, userid);
	}

}
