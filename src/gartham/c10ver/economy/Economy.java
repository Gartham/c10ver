package gartham.c10ver.economy;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

public class Economy {

	private final File econDir;

	public Economy(File dir) {
		econDir = dir;
		econDir.mkdirs();
		if (!dir.isDirectory())
			throw new IllegalArgumentException("Could not create Economy directory.");
		for (File f : dir.listFiles())
			try {
				if (f.isDirectory()) {
					Account a = new Account(f);
					userAccounts.put(f.getName(), a);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private final Map<String, Account> userAccounts = new HashedMap<>();

	public synchronized Account getAccount(String userID) throws RuntimeException {
		// TODO Synch over user instead.
		if (!userAccounts.containsKey(userID))
			try {
				userAccounts.put(userID, new Account(new File(econDir, userID)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		return userAccounts.get(userID);
	}

	public boolean hasAccount(String userID) {
		return userAccounts.containsKey(userID);
	}

}
