package gartham.c10ver.economy;

import java.io.File;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

public class Economy {

	private final File econDir;

	public Economy(File dir) {
		econDir = dir;
		File[] userFolder = dir.listFiles();
		if (userFolder != null)
			for (File f : userFolder)
				if (f.isDirectory()) {
					userAccounts.put(f.getName(), new Account(f));
				}
	}

	private final Map<String, Account> userAccounts = new HashedMap<>();

	public synchronized Account getAccount(String userID) throws RuntimeException {
		// TODO Synch over user instead.
		if (!userAccounts.containsKey(userID))
			userAccounts.put(userID, new Account(new File(econDir, userID)));
		return userAccounts.get(userID);
	}

	public boolean hasAccount(String userID) {
		return userAccounts.containsKey(userID);
	}

}
