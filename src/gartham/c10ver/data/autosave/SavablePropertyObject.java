package gartham.c10ver.data.autosave;

import java.io.File;

import gartham.c10ver.data.PropertyObject;
import gartham.c10ver.utils.DataUtils;

public class SavablePropertyObject extends PropertyObject {

	private final File saveLocation;

	public File getSaveLocation() {
		return saveLocation;
	}

	public SavablePropertyObject(File saveLocation) {
		super(DataUtils.loadObj(saveLocation));
		this.saveLocation = saveLocation;
	}

	@Override
	public void change() {
		super.change();
		DataUtils.save(getProperties(), saveLocation);
	}
}
