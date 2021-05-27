package gartham.c10ver.economy.accolades;

import java.math.BigInteger;

public enum AccoladeType {
	;
	private final String name, icon, description;
	private final BigInteger value;

	private AccoladeType(String name, String icon, String description, BigInteger value) {
		this.name = name;
		this.icon = icon;
		this.description = description;
		this.value = value;
	}

	private AccoladeType(String name, String icon, String description, long value) {
		this.name = name;
		this.icon = icon;
		this.description = description;
		this.value = BigInteger.valueOf(value);
	}

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}

	public String getDescription() {
		return description;
	}

	public BigInteger getValue() {
		return value;
	}

}
