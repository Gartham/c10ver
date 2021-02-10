package gartham.c10ver.economy;

import java.io.File;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.users.User;

public class Economy {

	private final File econDir;

	public Economy(File dir) {
		econDir = dir;
		File[] userFolder = dir.listFiles();
		if (userFolder != null)
			for (File f : userFolder)
				if (f.isDirectory())
					users.put(f.getName(), new User(f));
	}

	private final Map<String, User> users = new HashedMap<>();

	public synchronized User getUser(String userID) throws RuntimeException {
		// TODO Synch over user instead.
		if (!users.containsKey(userID))
			users.put(userID, new User(new File(econDir, userID)));
		return users.get(userID);
	}

	public Account getAccount(String userID) {
		return getUser(userID).getAccount();
	}

	public Inventory getInventory(String userID) {
		return getUser(userID).getInventory();
	}

	public boolean hasAccount(String userID) {
		return users.containsKey(userID);
	}

}
