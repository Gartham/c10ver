package gartham.c10ver.economy.server;

import java.math.BigInteger;

import gartham.c10ver.data.PropertyObject;

public class ColorRole extends PropertyObject {
	private final Property<String> name = stringProperty("name"), id = stringProperty("id");
	private final Property<BigInteger> cost = bigIntegerProperty("cost");

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getID() {
		return id.get();
	}

	public void setID(String id) {
		this.id.set(id);
	}

	public BigInteger getCost() {
		return cost.get();
	}

	public void setCost(BigInteger cost) {
		this.cost.set(cost);
	}
}
