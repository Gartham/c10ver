package gartham.c10ver.economy.users;

import java.io.File;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.Owned;

public class UserSettings extends SavablePropertyObject implements Owned<EconomyUser> {

	private final Property<Boolean> randomRewardsNotifyingEnabled = booleanProperty("rrn", false),
			voteRemindersEnabled = booleanProperty("vr", false);

	public boolean isRandomRewardsNotifyingEnabled() {
		return randomRewardsNotifyingEnabled.get();
	}

	public boolean isVoteRemindersEnabled() {
		return voteRemindersEnabled.get();
	}

	public void setRandomRewardsNotifyingEnabled(boolean enabled) {
		randomRewardsNotifyingEnabled.set(enabled);
	}

	public void setVoteRemindersEnabled(boolean enabled) {
		voteRemindersEnabled.set(enabled);
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

	public boolean flipVoteRemindersEnabled() {
		return voteRemindersEnabled.set(!voteRemindersEnabled.get()).get();
	}

	public Property<Boolean> randomRewardsNotifyingEnabledProperty() {
		return randomRewardsNotifyingEnabled;
	}

	public Property<Boolean> voteRemindersEnabledProperty() {
		return voteRemindersEnabled;
	}

	private final EconomyUser owner;

	public UserSettings(File userDir, EconomyUser owner) {
		this(userDir, true, owner);
	}

	public UserSettings(File userDir, boolean load, EconomyUser owner) {
		super(new File(userDir, "settings.txt"));
		this.owner = owner;
		if (load)
			load();
	}

	@Override
	public EconomyUser getOwner() {
		return owner;
	}

}
