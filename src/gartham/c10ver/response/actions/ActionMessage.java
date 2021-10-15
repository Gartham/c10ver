package gartham.c10ver.response.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.consumers.MessageReactionInputConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public final class ActionMessage<R extends ActionReaction, B extends ActionButton> {
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

	private final ArrayList<R> reactions;
	private final ArrayList<B> buttons;

	public List<B> getButtons() {
		return buttons;
	}

	public ActionMessage(Iterable<R> reactions, Iterable<B> buttons) {
		this.reactions = new ArrayList<>();
		this.buttons = new ArrayList<>();
		for (R r : reactions)
			this.reactions.add(r);
		for (B b : buttons)
			this.buttons.add(b);
	}

	public ActionMessage(Collection<R> reactions, Collection<B> buttons) {
		this.reactions = new ArrayList<>(reactions);
		this.buttons = new ArrayList<>(buttons);
	}

	@SafeVarargs
	public ActionMessage(R[] reactions, B... buttons) {
		(this.reactions = new ArrayList<>()).ensureCapacity(reactions.length);
		(this.buttons = new ArrayList<>()).ensureCapacity(buttons.length);
		for (R r : reactions)
			this.reactions.add(r);
		for (B b : buttons)
			this.buttons.add(b);
	}

	@SafeVarargs
	public ActionMessage(B[] buttons, R... reactions) {
		(this.reactions = new ArrayList<>()).ensureCapacity(reactions.length);
		(this.buttons = new ArrayList<>()).ensureCapacity(buttons.length);
		for (R r : reactions)
			this.reactions.add(r);
		for (B b : buttons)
			this.buttons.add(b);
	}

	public ActionMessage() {
		reactions = new ArrayList<>();
		buttons = new ArrayList<>();
	}

	public List<R> getReactions() {
		return reactions;
	}

	/**
	 * Attaches the {@link ActionReaction}s stored in this {@link ActionMessage}
	 * object to the specified {@link Message} and sets up the appropriate listeners
	 * to wait for the target user to "act" on the message. This method <b>does
	 * not</b> attach {@link ActionButton}s, as the discord API does not allow you
	 * to "modify" a message and add buttons to it (afaik lol).
	 * 
	 * @param clover Instance of {@link Clover} to use.
	 * @param msg    The {@link Message} to attach the buttons (emojis) to.
	 * @param target The target user. Only this user will be able to "act" on the
	 *               message (i.e. only this user will trigger the bot when they
	 *               react).
	 */
	public final void attach(Clover clover, Message msg, User target) {
		if (!reactions.isEmpty()) {
			for (int i = 0; i < reactions.size(); i++) {
				String customEmoji = reactions.get(i).getEmoji();
				msg.addReaction(customEmoji == null ? EMOJIS[i] : customEmoji).queue();
			}
			clover.getEventHandler().getReactionAdditionProcessor().registerInputConsumer(
					((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, processor, consumer) -> {
						for (int i = 0; i < reactions.size(); i++) {
							String customEmoji = reactions.get(i).getEmoji();
							if (event.getReactionEmote().getEmoji()
									.equals(customEmoji == null ? EMOJIS[i] : customEmoji)) {
								reactions.get(i).accept(new ActionInvocation(event, this, clover));
								return true;
							}
						}
						return false;
					}).filter(target, msg).oneTime());
		}
	}

	/**
	 * Creates a new message (sends the {@link MessageAction} provided) setting the
	 * stored {@link ActionButton}s in it, and
	 * {@link #attach(Clover, Message, User)}es the {@link ActionReaction}s to it.
	 * 
	 * @param clover Instance of {@link Clover} to use.
	 * @param ma     The {@link MessageAction} containing the message to be sent.
	 * @param target The target user. Only this user will be able to "act" on the
	 *               message (i.e. only this user will trigger the bot when they
	 *               react).
	 */
	public final void create(Clover clover, MessageAction ma, User target) {
		if (!buttons.isEmpty()) {
			List<ActionRow> rows = new ArrayList<>();
			List<Component> comps = new ArrayList<>();
			for (int i = 0; i < buttons.size(); i++)
				if (comps.size() == 5) {
					rows.add(ActionRow.of(comps));
					comps.clear();
				}
			ma.setActionRows(rows);
		}
		ma.queue(t -> {
			if (!reactions.isEmpty()) {
				for (int i = 0; i < reactions.size(); i++) {
					String customEmoji = reactions.get(i).getEmoji();
					t.addReaction(customEmoji == null ? EMOJIS[i] : customEmoji).queue();
				}
				clover.getEventHandler().getReactionAdditionProcessor().registerInputConsumer(
						((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, processor, consumer) -> {
							for (int i = 0; i < reactions.size(); i++) {
								String customEmoji = reactions.get(i).getEmoji();
								if (event.getReactionEmote().getEmoji()
										.equals(customEmoji == null ? EMOJIS[i] : customEmoji)) {
									reactions.get(i).accept(new ActionInvocation(event, this, clover));
									return true;
								}
							}
							return false;
						}).filter(target, t).oneTime());
			}
		});
	}

}
