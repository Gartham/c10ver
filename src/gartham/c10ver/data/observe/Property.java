package gartham.c10ver.data.observe;

import java.util.ArrayList;
import java.util.List;

import gartham.c10ver.data.autosave.Changeable;

public class Property<V> {
	private V value;
	private final List<PropertyObserver<? super V>> observers = new ArrayList<>();

	/**
	 * Sets the value of this property. Notifies listeners after the process.
	 * 
	 * @param value The new value of the property.
	 */
	public void setValue(V value) {
		setSilent(value);
		for (var o : observers)
			o.observe(this, value);
	}

	/**
	 * Returns the value that this property currently holds.
	 * 
	 * @return The value held by this property.
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Sets the value of this {@link Property} without notifying any listeners.
	 * 
	 * @param value The property's new value.
	 */
	public void setSilent(V value) {
		this.value = value;
	}

	public void register(PropertyObserver<? super V> o) {
		observers.add(o);
	}

	public void unregister(PropertyObserver<? super V> o) {
		observers.remove(o);
	}

	public void register(Observer o) {
		observers.add(o.toPropertyObserver());
	}

	public void unregister(Observer o) {
		observers.remove(o.toPropertyObserver());
	}

}
