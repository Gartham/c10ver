package gartham.c10ver.transactions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.json.JSONArray;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

import gartham.c10ver.economy.items.Item;

public class Transaction {

	public final class Entry {
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

	private final List<Entry> items;
	private final String userID;

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
			String iname = obj.getString("item_name" + i);
			int itemCount = Integer.valueOf(obj.getString("quantity" + i));
		}
	}

	public JSONValue toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("items", new JSONArray(JavaTools.mask(items, Entry::toJSON)));
		obj.put("id", userID);
		return obj;

	}

}
