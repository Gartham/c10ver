package gartham.c10ver.response.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.commands.consumers.MessageReactionInputConsumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
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

	@SafeVarargs
	public ActionMessage(R... reactions) {
		(this.reactions = new ArrayList<>()).ensureCapacity(reactions.length);
		this.buttons = new ArrayList<>();
		for (R r : reactions)
			this.reactions.add(r);
	}

	@SafeVarargs
	public ActionMessage(B... buttons) {
		(this.buttons = new ArrayList<>()).ensureCapacity(buttons.length);
		this.reactions = new ArrayList<>();
		for (B b : buttons)
			this.buttons.add(b);
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
								reactions.get(i).accept(new ActionReactionInvocation(event, this, clover));
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
		create(ma, target, clover.getEventHandler().getReactionAdditionProcessor(),
				clover.getEventHandler().getButtonClickProcessor());
	}

	/**
	 * <p>
	 * Sends this {@link ActionMessage} as the provided JDA {@link MessageAction}.
	 * </p>
	 * <h3>Reactions</h3>
	 * <p>
	 * If this {@link ActionMessage} has any {@link #getReactions() reaction}
	 * actions registered to it, then those will automatically be added to the
	 * discord message that gets sent by this method, and the provided
	 * {@link InputProcessor reactionProcessor} will be used to listen for users
	 * clicking an action (by clicking one of the reactions).
	 * </p>
	 * <h3>Buttons</h3>
	 * <p>
	 * If this {@link ActionMessage} has any {@link #getButtons() button} actions
	 * assigned to it, then those will be added to the {@link MessageAction}
	 * provided, and the discord message will then show buttons to users once it's
	 * sent. The provided {@link InputProcessor ButtonClickProcessor} is used to
	 * listen to buttons being clicked by users.
	 * </p>
	 * <h3>Nulls</h3>
	 * <p>
	 * The <code>reactionProcessor</code> or <code>buttonClickProcessor</code> can
	 * be <code>null</code> so long as this {@link ActionMessage} doesn't have any
	 * {@link #getReactions() reactions} or {@link #getButtons() buttons}
	 * registered, respectively. If this {@link ActionMessage} has either reactions
	 * or buttons, and the <code>reactionProcessor</code> or
	 * <code>buttonClickProcessor</code> are <code>null</code>, respectively, this
	 * method will throw an error.
	 * </p>
	 * 
	 * @param ma                   The message to send and attach buttons and/or
	 *                             reactions to.
	 * @param target               The targeted user who is expected to invoke the
	 *                             actions represented by the buttons or reactions.
	 * @param reactionProcessor    The reaction addition processor to use to listen
	 *                             to reactions with.
	 * @param buttonClickProcessor The button click processor to use to listen to
	 *                             button clicks with.
	 */
	public final void create(MessageAction ma, User target, InputProcessor<MessageReactionAddEvent> reactionProcessor,
			InputProcessor<ButtonClickEvent> buttonClickProcessor) {
		if (!buttons.isEmpty()) {
			List<ActionRow> rows = new ArrayList<>();
			List<Component> comps = new ArrayList<>();
			for (B b : getButtons()) {
				comps.add(b.getComponent());
				if (comps.size() == 5) {
					rows.add(ActionRow.of(comps));
					comps.clear();
				}
			}
			if (!comps.isEmpty())
				rows.add(ActionRow.of(comps));
			ma.setActionRows(rows);
		}
		ma.queue(t -> {
			if (!reactions.isEmpty()) {
				for (int i = 0; i < reactions.size(); i++) {
					String customEmoji = reactions.get(i).getEmoji();
					t.addReaction(customEmoji == null ? EMOJIS[i] : customEmoji).queue();
				}
				reactionProcessor.registerInputConsumer(
						((MessageReactionInputConsumer<MessageReactionAddEvent>) (event, processor, consumer) -> {
							for (int i = 0; i < reactions.size(); i++) {
								String customEmoji = reactions.get(i).getEmoji();
								if (event.getReactionEmote().getEmoji()
										.equals(customEmoji == null ? EMOJIS[i] : customEmoji)) {
									reactions.get(i).accept(new ActionReactionInvocation(event, this, reactionProcessor,
											buttonClickProcessor));
									return true;
								}
							}
							return false;
						}).filter(target, t).oneTime());
			}
			if (!buttons.isEmpty())
				buttonClickProcessor
						.registerInputConsumer(((InputConsumer<ButtonClickEvent>) (event, processor, consumer) -> {
							if (event.getMessage().equals(t))
								for (B b : buttons)
									if (b.getComponent().getId().equals(event.getComponentId())) {
										if (event.getUser().equals(target)) {
											b.getAction().accept(new ActionButtonInvocation(event, this,
													reactionProcessor, buttonClickProcessor));
											try {
												event.editButton(event.getButton().asDisabled()).queue();
											} catch (Exception e) {
												System.err.println(
														"Exception (possibly okay) when handling button click: "
																+ e.getMessage());
											}
											return true;
										} else
											event.reply(b.getNontargetReply());
									}
							return false;
						}).oneTime());
		});
	}

}
