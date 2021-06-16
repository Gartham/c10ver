package gartham.c10ver.economy.users;

import java.io.File;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.Owned;

public class UserSettings extends SavablePropertyObject implements Owned<User> {

	private final Property<Boolean> randomRewardsNotifyingEnabled = booleanProperty("rrn", false);

	public boolean isRandomRewardsNotifyingEnabled() {
		return randomRewardsNotifyingEnabled.get();
	}

	public void setRandomRewardsNotifyingEnabled(boolean enabled) {
		randomRewardsNotifyingEnabled.set(enabled);
	}

	/**
	 * Flips the boolean property of 'whether random rewards will notify the user'
	 * and returns the new value of this property.
	 * 
	 * @return What the property was flipped to.
	 */
	public boolean flipRandomRewardsNotifyingEnabled() {
		return randomRewardsNotifyingEnabled.set(!randomRewardsNotifyingEnabled.get()).get();
	}

	public Property<Boolean> randomRewardsNotifyingEnabledProperty() {
		return randomRewardsNotifyingEnabled;
	}

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
