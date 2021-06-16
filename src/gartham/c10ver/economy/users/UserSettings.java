package gartham.c10ver.economy.users;

import java.io.File;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.Owned;

public class UserSettings extends SavablePropertyObject implements Owned<User> {

	private final Property<Boolean> notifyOfRandomRewards = booleanProperty("rrn", false);

	private final User owner;

	public UserSettings(File userDir, User owner) {
		this(userDir, true, owner);
	}

	public UserSettings(File userDir, boolean load, User owner) {
		super(new File(userDir, "settings.txt"));
		this.owner = owner;
		if (load)
			load();
	}

	@Override
	public User getOwner() {
		return owner;
	}

}
