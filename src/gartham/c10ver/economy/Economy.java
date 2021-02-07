package gartham.c10ver.economy;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

public class Economy {
	private final Map<String, Account> userAccounts = new HashedMap<>();

	public synchronized Account getAccount(String userID) {
		// TODO Synch over user instead.
		if (!userAccounts.containsKey(userID))
			userAccounts.put(userID, new Account());
		return userAccounts.get(userID);
	}

	public boolean hasAccount(String userID) {
		return userAccounts.containsKey(userID);
	}

}
