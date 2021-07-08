package gartham.c10ver.actions;

import java.util.ArrayList;
import java.util.List;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.consumers.MessageReactionInputConsumer;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public abstract class ActionMessage {
	static final String[] EMOJIS = { "\u0030\\uFE0F\u20E3", "\u0031\uFE0F\u20E3", "\u0032\uFE0F\u20E3",
			"\u0033\uFE0F\u20E3", "\u0034\uFE0F\u20E3", "\u0035\uFE0F\u20E3", "\u0036\uFE0F\u20E3",
			"\u0037\uFE0F\u20E3", "\u0038\uFE0F\u20E3", "\u0039\uFE0F\u20E3", "\u0040\uFE0F\u20E3" };

	public static String[] emojis() {
		return EMOJIS.clone();
	}

	public static Iterable<String> emojiItr() {
		return JavaTools.iterable(EMOJIS);
	}

	private final List<Action> actions = new ArrayList<>();

	public ActionMessage(Action... actions) {
		for (var a : actions)
			this.actions.add(a);
	}

	public List<Action> getActions() {
		return actions;
	}

	public abstract MessageEmbed embed();

	public void send(Clover clover, MessageChannel msg) {
		msg.sendMessage(embed()).queue(t -> {
			if (!actions.isEmpty()) {
				for (int i1 = 0; i1 < actions.size(); i1++)
					t.addReaction(EMOJIS[i1]).queue();
				clover.getEventHandler().getReactionAdditionProcessor().registerInputConsumer(
						((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, processor, consumer) -> {
							for (int i2 = 0; i2 < actions.size(); i2++)
								if (event.getReactionEmote().getEmoji().equals(EMOJIS[i2])) {
									actions.get(i2).accept(new ActionInvocation(event, this, clover));
									return true;
								}
							return false;
						}).filter(t.getAuthor(), t.getChannel()).oneTime());
			}

		});
	}

}
