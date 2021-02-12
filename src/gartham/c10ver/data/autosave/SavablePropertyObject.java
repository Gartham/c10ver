package gartham.c10ver.data.autosave;

import java.io.File;

import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.utils.Utilities;

public class SavablePropertyObject extends PropertyObject {

	private final File saveLocation;

	public File getSaveLocation() {
		return saveLocation;
	}

	public SavablePropertyObject(File saveLocation) {
		load(Utilities.loadObj(saveLocation));
		this.saveLocation = saveLocation;
		register(() -> Utilities.save(toJSON(), saveLocation));
	}

}
