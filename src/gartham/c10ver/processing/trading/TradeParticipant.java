package gartham.c10ver.processing.trading;

import java.math.BigInteger;
import java.util.Iterator;

import gartham.c10ver.economy.User;
import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.utils.ItemList;
import gartham.c10ver.economy.items.utils.ItemList.Entry;

public class TradeParticipant {
	private final User ecouser;
	private final ItemList items = new ItemList();
	private BigInteger cloves = BigInteger.ZERO;

	private boolean finished;

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public User getEcouser() {
		return ecouser;
	}

	public ItemList getItems() {
		return items;
	}

	public TradeParticipant(User ecouser) {
		this.ecouser = ecouser;
	}

	public BigInteger getCloves() {
		return cloves;
	}

	public void setCloves(BigInteger cloves) {
		this.cloves = cloves;
	}

	public void add(BigInteger cloves) {
		this.cloves = this.cloves.add(cloves);
		if (this.cloves.compareTo(BigInteger.ZERO) < 0)
			this.cloves = BigInteger.ZERO;

	}

	public void sub(BigInteger cloves) {
		this.cloves = this.cloves.subtract(cloves);
		if (this.cloves.compareTo(BigInteger.ZERO) < 0)
			this.cloves = BigInteger.ZERO;
	}

	public void add(Item item, BigInteger amt) {
		for (var x : items.getItems())
			if (x.getItem().stackable(item)) {
				x.setCount(x.getCount().add(amt));
				return;
			}
		items.new Entry<>(item, amt);
	}

	public void remove(Item item, BigInteger amt) {
		for (Iterator<Entry<?>> iterator = items.getItems().iterator(); iterator.hasNext();) {
			var x = iterator.next();
			if (x.getItem().stackable(item)) {
				x.setCount(x.getCount().subtract(amt));
				if (x.getCount().compareTo(BigInteger.ZERO) <= 0)
					iterator.remove();
				return;
			}
		}
	}

	public void add(Item item) {
		add(item, 1);
	}

	public void add(Item item, long amount) {
		add(item, BigInteger.valueOf(amount));
	}

}
