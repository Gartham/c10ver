package gartham.c10ver.games.rpg.wilderness;

public enum Emoji {
	BLACK("\u2B1B"), BLACK_SQUARE_BUTTON("\uD83D\uDD32"), BROWN("\uD83D\uDFEB"), RED("\uD83D\uDFE5"),
	ORANGE("\uD83D\uDFE7"), YELLOW("\uD83D\uDFE8"), GREEN("\uD83D\uDFE9"), BLUE("\uD83D\uDFE6"), PURPLE("\uD83D\uDFEA"),
	WHITE_SQUARE_BUTTON("\uD83D\uDD33"), WHITE("\u2B1C"), GREEN_WITH_INSCRIBED_X("\u274E");

	private final String value;

	private Emoji(String emoji) {
		value = emoji;
	}

	public String getValue() {
		return value;
	}
}
