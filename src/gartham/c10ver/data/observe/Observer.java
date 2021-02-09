package gartham.c10ver.data.observe;

public interface Observer {
	void observe();

	default <V> PropertyObserver<V> toPropertyObserver() {
		return (property, value) -> Observer.this.observe();
	}
}
