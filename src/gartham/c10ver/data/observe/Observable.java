package gartham.c10ver.data.observe;

import java.util.ArrayList;
import java.util.List;

public class Observable<V> {
	private final List<Observer<? super V>> observers = new ArrayList<>(2);

	public void register(Observer<? super V> observer) {
		observers.add(observer);
	}

	public void unregister(Observer<? super V> observer) {
		observers.remove(observer);
	}

	protected final void change(V oldVal, V newVal) {
		for (Observer<? super V> o : observers)
			o.observe(oldVal, newVal);
	}

}
