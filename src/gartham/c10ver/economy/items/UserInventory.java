package gartham.c10ver.economy.items;

import java.io.File;

import gartham.c10ver.economy.items.Inventory.Entry;

public class UserInventory {
	private final Inventory inventory = new Inventory();
	private final File invdir;
	
	public UserInventory(File userDir) {
		invdir = new File(userDir, "inventory");
		inventory.load(invdir);
	}
}
