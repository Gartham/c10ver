package gartham.c10ver.economy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import gartham.c10ver.Clover;
import gartham.c10ver.economy.items.UserInventory;

public class Economy {

	private final File root;
	private final Clover clover;

	public Clover getClover() {
		return clover;
	}

	public Economy(File dir, Clover clover) {
		this.clover = clover;
		root = dir;
		File[] userFolder = getUserDir().listFiles();
		if (userFolder != null)
			for (File f : userFolder)
				if (f.isDirectory())
					users.put(f.getName(), new User(f, this));
		File[] serverFolder = getServersDir().listFiles();
		if (serverFolder != null)
			for (File f : serverFolder)
				if (f.isDirectory())
					servers.put(f.getName(), new Server(f));
	}

	private final Map<String, User> users = new HashedMap<>();
	private final Map<String, Server> servers = new HashMap<>();

	public synchronized User getUser(String userID) throws RuntimeException {
		// TODO Synch over user instead.
		if (!users.containsKey(userID))
			users.put(userID, new User(new File(getUserDir(), userID), this));
		return users.get(userID);
	}

	public synchronized Server getServer(String serverID) throws RuntimeException {
		if (!servers.containsKey(serverID))
			servers.put(serverID, new Server(new File(getServersDir(), serverID)));
		return servers.get(serverID);
	}

	public UserAccount getAccount(String userID) {
		return getUser(userID).getAccount();
	}

	public UserInventory getInventory(String userID) {
		return getUser(userID).getInventory();
	}

	public boolean hasAccount(String userID) {
		return hasUser(userID);
	}

	public boolean hasUser(String userID) {
		return users.containsKey(userID);
	}

	public boolean hasServer(String serverID) {
		return servers.containsKey(serverID);
	}

	public File getUserDir() {
		return new File(root, "users");
	}

	public File getServersDir() {
		return new File(root, "servers");
	}

}
