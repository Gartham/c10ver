package gartham.c10ver.data.observe;

public interface PropertyObserver<V> {
	void observe(Property<? extends V> property, V value);
}
