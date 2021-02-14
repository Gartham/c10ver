package gartham.c10ver.economy;

import java.io.File;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import gartham.c10ver.Clover;
import gartham.c10ver.economy.items.Inventory;
import gartham.c10ver.users.User;

public class Economy {

	private final File econDir;
	private final Clover clover;

	public Clover getClover() {
		return clover;
	}

	public Economy(File dir, Clover clover) {
		this.clover = clover;
		econDir = dir;
		File[] userFolder = dir.listFiles();
		if (userFolder != null)
			for (File f : userFolder)
				if (f.isDirectory())
					users.put(f.getName(), new User(f, this));
	}

	private final Map<String, User> users = new HashedMap<>();

	public synchronized User getUser(String userID) throws RuntimeException {
		// TODO Synch over user instead.
		if (!users.containsKey(userID))
			users.put(userID, new User(new File(econDir, userID), this));
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
