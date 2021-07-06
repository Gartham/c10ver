package gartham.c10ver.games.rpg.creatures;

import java.io.File;

import gartham.c10ver.data.autosave.SavablePropertyObject;

public class CreatureBox extends SavablePropertyObject {

	private final Property<Integer> capacity = intProperty("capacity", 15);

	public int getCapacity() {
		return capacity.get();
	}

	public void setCapacity(int capacity) {
		this.capacity.set(capacity);
	}

	public void increaseCapacity(int amount) {
		setCapacity(getCapacity() + amount);
	}

	public CreatureBox(File file) {
		this(file, true);
	}

	public CreatureBox(File file, boolean load) {
		super(file);
		if (load)
			load();
	}
}
