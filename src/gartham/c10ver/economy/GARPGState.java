package gartham.c10ver.economy;

import java.io.File;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.users.EconomyUser;

public class GARPGState extends SavablePropertyObject {

	private final EconomyUser user;
	private boolean active;

	public GARPGState(File saveLocation, EconomyUser user) {
		super(saveLocation);
		this.user = user;
	}

	public EconomyUser getUser() {
		return user;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
