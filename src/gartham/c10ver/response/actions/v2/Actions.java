package gartham.c10ver.response.actions.v2;

import java.util.function.Consumer;

import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.MessageReactionInputConsumer;
import gartham.c10ver.response.actions.ActionReactionInvocation;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class Actions {
	/**
	 * Reacts to the specified {@link Message} with the specified emoji and listens
	 * (via the {@link InputProcessor}) for other reactions to the specified message
	 * with the specified emoji. Calls the {@link Consumer} provided when a reaction
	 * takes place.
	 * 
	 * @param emoji     The emoji to react with.
	 * @param message   The message to react to.
	 * @param handler   The handler to call when a new reaction (of the specified
	 *                  <code>emoji</code>) is added (to the specified
	 *                  <code>message</code>).
	 * @param processor The processor that is used to listen for new reactions.
	 */
	public static void attach(String emoji, Message message, Consumer<MessageReactionAddEvent> handler,
			InputProcessor<MessageReactionAddEvent> processor) {
		// TODO System should be able to handle multiple reactions.
		// TODO System should be able to easily filter. (Perhaps return an object.)
		processor.registerInputConsumer(
				((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, processor, consumer) -> {
					for (int i = 0; i < reactions.size(); i++) {
						String customEmoji = reactions.get(i).getEmoji();
						if (event.getReactionEmote().getEmoji().equals(customEmoji == null ? EMOJIS[i] : customEmoji)) {
							reactions.get(i).accept(
									new ActionReactionInvocation(event, this, reactionProcessor, buttonClickProcessor));
							return true;
						}
					}
					return false;
				}).filter(target, t).oneTime());
	}
}
