package gartham.c10ver.data.observe;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.data.autosave.Changeable;

public class Observable implements Changeable {
	private List<Observer> observers = new ArrayList<>();

	public void register(Observer o) {
		observers.add(o);
	}

	public void unregister(Observer o) {
		observers.remove(o);
	}

	@Override
	public void change() {
		for (Observer o : observers)
			o.observe();
	}

}
