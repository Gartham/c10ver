package gartham.c10ver.economy.items;

public enum ItemCategory {
	CRATE("Crates", "<:crate:808762616456675338>"), FOOD("Foods", "\uD83C\uDF55"), MISC("Miscelaneous", "\u2753");

	private final String displayName, icon;

	private ItemCategory(String displayName, String icon) {
		this.displayName = displayName;
		this.icon = icon;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getIcon() {
		return icon;
	}
}
