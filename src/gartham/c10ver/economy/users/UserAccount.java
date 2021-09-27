package gartham.c10ver.economy.users;

import java.io.File;
import java.math.BigInteger;

import gartham.c10ver.economy.Account;
import gartham.c10ver.economy.Owned;

public class UserAccount extends Account implements Owned<EconomyUser> {
	private final Property<BigInteger> totalEarnings = bigIntegerProperty("total-earnings", BigInteger.ZERO);

	public void setTotalEarnings(BigInteger earnings) {
		totalEarnings.set(earnings);
	}

	public BigInteger getTotalEarnings() {
		return totalEarnings.get();
	}

	public BigInteger addTotalEarnings(BigInteger earnings) {
		return totalEarnings.set(totalEarnings.get().add(earnings)).get();
	}

	private final EconomyUser user;

	public EconomyUser getUser() {
		return user;
	}

	@Override
	public EconomyUser getOwner() {
		return getUser();
	}

	public UserAccount(File userDirectory, EconomyUser user) {
		this(userDirectory, true, user);
	}

	protected UserAccount(File userDirectory, boolean load, EconomyUser user) {
		super(new File(userDirectory, "main-account.txt"));
		this.user = user;
		if (load)
			load();
	}
}
