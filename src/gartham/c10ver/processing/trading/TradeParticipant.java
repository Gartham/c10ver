package gartham.c10ver.processing.trading;

import java.math.BigInteger;
import java.util.Map;

import gartham.c10ver.economy.items.Item;
import gartham.c10ver.economy.items.utils.ItemList;
import gartham.c10ver.economy.items.utils.ItemList.Entry;
import gartham.c10ver.economy.users.EconomyUser;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;

public class TradeParticipant {
	private final EconomyUser ecouser;
	private final ItemList items = new ItemList();
	private BigInteger cloves = BigInteger.ZERO;

	/**
	 * Whether this participant is finished adding what they needed to to the trade.
	 */
	private boolean finished;

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public EconomyUser getEcouser() {
		return ecouser;
	}

	public ItemList getItems() {
		return items;
	}

	public TradeParticipant(EconomyUser ecouser) {
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

	public <I extends Item> Entry<I> add(I item) {
		return add(item, 1);
	}

	public <I extends Item> Entry<I> add(I item, long amount) {
		return items.add(item, BigInteger.valueOf(amount));
	}

	public EmbedBuilder getTrade(EmbedBuilder e) {
		return e.setAuthor(ecouser.getUser().getAsTag(), null, ecouser.getUser().getEffectiveAvatarUrl())
				.setDescription(Utilities.list(cloves, items.getItemBunches(), Map.of()));
	}

}
