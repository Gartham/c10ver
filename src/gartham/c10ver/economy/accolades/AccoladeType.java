package gartham.c10ver.economy.accolades;

public enum AccoladeType {
	;
	private final String name, icon;

	private AccoladeType(String name, String icon) {
		this.name = name;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}
}
