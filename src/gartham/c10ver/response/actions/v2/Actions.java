package gartham.c10ver.response.actions.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.MessageReactionInputConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

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

	public static void attach(Message message, Consumer<String> handler,
			InputProcessor<MessageReactionAddEvent> processor, String... reactions) {
		attach(message, handler, processor, Arrays.asList(reactions));
	}

	public static void attach(Message message, Consumer<String> handler,
			InputProcessor<MessageReactionAddEvent> processor, int numberReactions, String... reactions) {
		attach(message, handler, processor, numberReactions, Arrays.asList(reactions));
	}

	public static void attach(Message message, Consumer<String> handler,
			InputProcessor<MessageReactionAddEvent> processor, int numberReactions, List<String> reactions) {
		attach(message, handler, processor, JavaTools.combine(new ArrayList<>(reactions.size() + numberReactions),
				numberEmojis(numberReactions), reactions));
	}

	public static void attach(Message message, Consumer<String> handler,
			InputProcessor<MessageReactionAddEvent> processor, List<String> reactions) {
		reactions = new ArrayList<>(reactions);
		reactions.replaceAll(Actions::normalizeEmoji);
		final var x = reactions;
		if (!reactions.isEmpty()) {
			for (var s : reactions)
				message.addReaction(s).queue();
			processor.registerInputConsumer(
					((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, p, consumer) -> {
						if (event.getMessageIdLong() != message.getIdLong())
							return false;
						for (var s : x) {
							if (s.equalsIgnoreCase(event.getReactionEmote().getName())) {
								handler.accept(s);
								return true;
							}
						}
						return false;
					}).oneTime());
		}
	}

	public static String normalizeEmoji(String emoji) {
		if ((emoji = emoji.toLowerCase()).startsWith("a:"))
			if (emoji.length() == 2)
				throw new IllegalArgumentException();
			else
				emoji.substring(2);
		return emoji;
	}

}
