package gartham.c10ver.economy;

import java.io.File;
import java.math.BigInteger;

public class UserAccount extends Account implements Owned<User> {
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

	private final User user;

	public User getUser() {
		return user;
	}

	@Override
	public User getOwner() {
		return getUser();
	}

	public UserAccount(File userDirectory, User user) {
		this(userDirectory, true, user);
	}

	protected UserAccount(File userDirectory, boolean load, User user) {
		super(new File(userDirectory, "main-account.txt"));
		this.user = user;
		if (load)
			load();
	}
}
