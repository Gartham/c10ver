package gartham.c10ver.games.rpg.creatures;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.utils.Utilities;

public class CreatureBox extends SavablePropertyObject {

	private final Property<Integer> capacity = intProperty("capacity", 15);
	private final File creatureDir;

	private final ArrayList<Creature> creatures = new ArrayList<>();

	public boolean add(Creature creature) {
		int pos = Collections.binarySearch(creatures, creature);
		if (pos < 0) {
			creatures.add(-pos - 1, creature);
			return true;
		} else
			return false;
	}

	/**
	 * Saves the creature specified by the provided index as belonging to this user
	 * in physical storage. More explicitly, this method finds the creature at the
	 * provided index in this object's {@link #creatures list of creatures} and
	 * saves it to physical storage in a folder owned by this {@link CreatureBox}.
	 * 
	 * @param index The index of the {@link Creature} in the {@link #creatures list
	 *              of creatures}.
	 */
	public void save(int index) {
		save(creatures.get(index));
	}

	private void save(Creature creature) {
		Utilities.save(creature.toJSON(), new File(creatureDir, "crtr-" + creature.getID().getHex()));
	}

	public int indexOf(Creature creature) {
		return Collections.binarySearch(creatures, creature);
	}

	public Creature get(int position) {
		return creatures.get(position);
	}

	public Creature remove(int position) {
		return creatures.remove(position);
	}

	public void remove(Creature creature) {
		int pos = Collections.binarySearch(creatures, creature);
		if (pos < 0)
			creatures.remove(-pos - 1);
	}

	public boolean contains(Creature creature) {
		return indexOf(creature) >= 0;
	}

	/**
	 * Saves this entire {@link CreatureBox}, including the {@link Creature}
	 * referenced by it.
	 */
	@Override
	public void save() {
		super.save();
		for (Creature c : creatures)
			save(c);
	}

	@Override
	public void load() {
		if (creatureDir.isDirectory())
			for (File f : creatureDir.listFiles())
				if (f.getName().startsWith("crtr-") && f.isFile())
					add(new Creature(Utilities.loadObj(f)));
		super.load();
	}

	public int getCapacity() {
		return capacity.get();
	}

	public void setCapacity(int capacity) {
		this.capacity.set(capacity);
	}

	public void increaseCapacity(int amount) {
		setCapacity(getCapacity() + amount);
	}

	public CreatureBox(File creatureDir) {
		this(creatureDir, true);
	}

	public CreatureBox(File creatureDir, boolean load) {
		super(new File(creatureDir, "box.txt"));
		this.creatureDir = creatureDir;
		if (load)
			load();
	}
}
