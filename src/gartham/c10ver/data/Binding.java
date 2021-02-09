package gartham.c10ver.data;

public interface Binding<V> {
	void propagateChange(V v);
}
