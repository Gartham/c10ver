package gartham.c10ver.response.actions.v2;

import java.util.List;

public final class Actions {

	private static final String[] EMOJIS = { "\u0030\uFE0F\u20E3", "\u0031\uFE0F\u20E3", "\u0032\uFE0F\u20E3",
			"\u0033\uFE0F\u20E3", "\u0034\uFE0F\u20E3", "\u0035\uFE0F\u20E3", "\u0036\uFE0F\u20E3",
			"\u0037\uFE0F\u20E3", "\u0038\uFE0F\u20E3", "\u0039\uFE0F\u20E3", "\u0040\uFE0F\u20E3" };

	private static final List<String> EMOJIS_LIST = List.of(EMOJIS);

	public static List<String> numberEmojis(int count) {
		return EMOJIS_LIST.subList(0, count);
	}

	public static List<String> numberEmojis(int start, int end) {
		return EMOJIS_LIST.subList(start, end);
	}

	public static List<String> numberEmojis() {
		return EMOJIS_LIST;
	}

}
