package gartham.c10ver.data.autosave;

import java.io.File;

import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.utils.Utilities;

public class SavablePropertyObject extends PropertyObject {

	private final File saveLocation;

	public File getSaveLocation() {
		return saveLocation;
	}

	public void load() {
		load(Utilities.loadObj(saveLocation));
	}

	public void save() {
		Utilities.save(toJSON(), saveLocation);
	}

	public SavablePropertyObject(File saveLocation) {
		this.saveLocation = saveLocation;
	}

}
