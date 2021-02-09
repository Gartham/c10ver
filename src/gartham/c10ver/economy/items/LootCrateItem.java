package gartham.c10ver.economy.items;

public class LootCrateItem extends Item {
	private final Property<CrateType> type = enumProperty("type", CrateType.DAILY, CrateType.class);
	{
		type.register(a -> icon.set(a.icon));
	}

	public CrateType getType() {
		return type.get();
	}

	public enum CrateType {
		DAILY, WEEKLY, MONTHLY;

		private final String icon;

		private CrateType() {
			this.icon = name().toLowerCase() + "crate";
		}

		private CrateType(String icon) {
			this.icon = icon + "crate";
		}

	}

	public LootCrateItem(CrateType type) {
		this.type.set(type);
		setIcon(type.icon);
	}
}
