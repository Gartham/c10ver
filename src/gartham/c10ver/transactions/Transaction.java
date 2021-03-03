package gartham.c10ver.transactions;

import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONNumber;
import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONValue;

public class Transaction {

	public enum Item {
		GLOBAL_MULT, SERVER_MULT;

		private static final Item[] VALUES = values();

		public JSONValue toJSON() {
			return new JSONNumber(ordinal());
		}

		public static Item valueOf(JSONValue json) {
			return VALUES[((JSONNumber) json).intValue()];
		}
	}

	private final Item item;
	private final BigDecimal amount;

	public Transaction(Item item, BigDecimal amount) {
		this.item = item;
		this.amount = amount;
	}

	public Transaction(JSONValue json) {
		var o = (JSONObject) json;
		item = Item.valueOf(o.get("item"));
		amount = new BigDecimal(o.getString("amt"));
	}

	public JSONValue toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("item", item.toJSON());
		obj.put("amt", amount.toString());
		return obj;

	}

}
