package gartham.c10ver.economy;

import java.io.File;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.users.EconomyUser;

public class GARPGData extends SavablePropertyObject {

	private final EconomyUser user;

	public GARPGData(File saveLocation, EconomyUser user) {
		super(saveLocation);
		this.user = user;
	}

}
