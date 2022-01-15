package gartham.c10ver.economy.items;

public enum ItemCategory {
	CRATE("Crates"), FOOD("Foods"), MISC("Miscelaneous");

	private final String displayName;

	private ItemCategory(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
