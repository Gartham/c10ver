package gartham.c10ver.response.utils;

import net.dv8tion.jda.api.interactions.components.Button;

public final class ResponseUtils {
	public static final String RIGHT_ALL = "\u23ED", LEFT_ALL = "\u23EE", RIGHT_ONE = "\u25B6", LEFT_ONE = "\u25C0";

	private ResponseUtils() {
	}

	public static String normalizeEmoji(String emoji) {
		if ((emoji = emoji.toLowerCase()).startsWith("a:"))
			if (emoji.length() == 2)
				throw new IllegalArgumentException();
			else
				emoji.substring(2);
		return emoji;
	}
	
	public static Button blockedButton(String id) {
		return Button.secondary(id, "\u200B").asDisabled();
	}

}
