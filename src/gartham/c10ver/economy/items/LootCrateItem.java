package gartham.c10ver.economy.items;

public class LootCrateItem extends Item {
	private final Property<CrateType> type = enumProperty("type", CrateType.DAILY, CrateType.class);

	public CrateType getType() {
		return type.get();
	}

	public enum CrateType {
		DAILY("daily"), WEEKLY("weekly"), MONTHLY("monthly");

		private final String icon;

		private CrateType(String icon) {
			this.icon = icon + "crate";
		}

	}

	public LootCrateItem(CrateType type) {
		this.type.set(type);
		setIcon(type.icon);
	}
}
