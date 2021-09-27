package gartham.c10ver.data.autosave;

public class AutosaveValue<T> {
	private T value;
	private final Changeable saveable;

	public AutosaveValue(T value, Changeable saveable) {
		this.value = value;
		this.saveable = saveable;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
		saveable.change();
	}

}
