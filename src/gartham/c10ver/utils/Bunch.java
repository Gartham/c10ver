package gartham.c10ver.utils;

import java.math.BigInteger;

public class Bunch<V> {
	private final V value;
	private final BigInteger count;

	public Bunch(V value, BigInteger count) {
		this.value = value;
		this.count = count;
	}

	public V getValue() {
		return value;
	}

	public BigInteger getCount() {
		return count;
	}

}
