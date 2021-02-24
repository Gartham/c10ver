package gartham.c10ver.economy.items.utility.foodstuffs;

import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONObject;

import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.utility.Usable;
import gartham.c10ver.economy.items.utils.ItemList;

public abstract class Foodstuff extends Item {

	private final Property<BigDecimal> multiplierValue = bigDecimalProperty("mult-val");

	public BigDecimal getMultiplierValue() {
		return multiplierValue.get();
	}

	protected void setMultiplierValue(BigDecimal multiplierValue) {
		this.multiplierValue.set(multiplierValue);
	}

	public Foodstuff(String type, JSONObject properties) {
		super(type, properties);
		load(multiplierValue, properties);
	}

	public Foodstuff(String type, String name, String icon) {
		super(type, name, icon);
	}

	public Foodstuff(String type) {
		super(type);
	}

}
