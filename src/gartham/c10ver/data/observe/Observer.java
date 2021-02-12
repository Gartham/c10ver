package gartham.c10ver.data.observe;

public interface Observer<V> {
	void observe(V oldValue, V newValue);
}
