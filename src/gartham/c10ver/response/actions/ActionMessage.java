package gartham.c10ver.response.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.consumers.MessageReactionInputConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class ActionMessage<A extends Action> {
	static final String[] EMOJIS = { "\u0030\uFE0F\u20E3", "\u0031\uFE0F\u20E3", "\u0032\uFE0F\u20E3",
			"\u0033\uFE0F\u20E3", "\u0034\uFE0F\u20E3", "\u0035\uFE0F\u20E3", "\u0036\uFE0F\u20E3",
			"\u0037\uFE0F\u20E3", "\u0038\uFE0F\u20E3", "\u0039\uFE0F\u20E3", "\u0040\uFE0F\u20E3" };

	public static String[] emojis() {
		return EMOJIS.clone();
	}

	public static Iterable<String> emojiItr() {
		return JavaTools.iterable(EMOJIS);
	}

	public static String getNumericEmoji(int index) {
		return EMOJIS[index];
	}

	private final List<A> actions = new ArrayList<>();

	@SafeVarargs
	public ActionMessage(A... actions) {
		this(JavaTools.iterable(actions));
	}

	public ActionMessage(Iterable<A> actions) {
		for (A a : actions)
			this.actions.add(a);
	}

	public ActionMessage(Iterator<A> actions) {
		while (actions.hasNext())
			this.actions.add(actions.next());
	}

	public List<A> getActions() {
		return actions;
	}

	public void attach(Clover clover, Message msg, User target) {
		if (!actions.isEmpty()) {
			for (int i = 0; i < actions.size(); i++) {
				String customEmoji = actions.get(i).getEmoji();
				msg.addReaction(customEmoji == null ? EMOJIS[i] : customEmoji).queue();
			}
			clover.getEventHandler().getReactionAdditionProcessor().registerInputConsumer(
					((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, processor, consumer) -> {
						for (int i = 0; i < actions.size(); i++) {
							String customEmoji = actions.get(i).getEmoji();
							if (event.getReactionEmote().getEmoji()
									.equals(customEmoji == null ? EMOJIS[i] : customEmoji)) {
								actions.get(i).accept(new ActionInvocation(event, this, clover));
								return true;
							}
						}
						return false;
					}).filter(target, msg).oneTime());
		}
	}

	public final void create(Clover clover, MessageAction ma, User target) {
		ma.queue(t -> {
			if (!actions.isEmpty()) {
				for (int i = 0; i < actions.size(); i++) {
					String customEmoji = actions.get(i).getEmoji();
					t.addReaction(customEmoji == null ? EMOJIS[i] : customEmoji).queue();
				}
				clover.getEventHandler().getReactionAdditionProcessor().registerInputConsumer(
						((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, processor, consumer) -> {
							for (int i = 0; i < actions.size(); i++) {
								String customEmoji = actions.get(i).getEmoji();
								if (event.getReactionEmote().getEmoji()
										.equals(customEmoji == null ? EMOJIS[i] : customEmoji)) {
									actions.get(i).accept(new ActionInvocation(event, this, clover));
									return true;
								}
							}
							return false;
						}).filter(target, t).oneTime());
			}

		});
	}

}
