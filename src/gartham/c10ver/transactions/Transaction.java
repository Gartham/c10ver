package gartham.c10ver.transactions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

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

	public static Transaction fromPaypalJSON(JSONValue json) {
		JSONObject obj = (JSONObject) json;
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
