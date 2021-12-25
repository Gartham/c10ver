package gartham.c10ver.response.menus;

import java.util.List;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.response.actions.ActionButton;
import gartham.c10ver.response.actions.ActionMessage;
import gartham.c10ver.response.actions.ActionReaction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/**
 * The {@link MenuMessage} class lets you send {@link ActionMessage}s (messages
 * that have emojis or buttons at the bottom which do something when clicked)
 * through a consistent format. A menu message not only adds the emojis/buttons
 * to the message after/before it's sent, a {@link MenuMessage} will actually
 * <i>build the message</i>, based off of the {@link ActionReaction}s the
 * message will let the user select. Essentially this class lets you present the
 * user with an {@link ActionMessage} but this class will also build a menu in
 * the message, possibly with descriptions, named entries, etc.
 * 
 * @author Gartham
 *
 */
public abstract class MenuMessage<R extends ActionReaction, B extends ActionButton> {

	protected abstract void buildEmbed(EmbedBuilder builder);

	private final ActionMessage<R, B> am;

	public ActionMessage<R, B> getAm() {
		return am;
	}

	public List<B> getButtons() {
		return am.getButtons();
	}

	public List<R> getReactions() {
		return am.getReactions();
	}

	public MenuMessage(ActionMessage<R, B> am) {
		this.am = am;
	}

	public final MessageEmbed embed() {
		var e = new EmbedBuilder();
		buildEmbed(e);
		return e.build();
	}

	public void send(InputProcessor<MessageReactionAddEvent> reactionProcessor,
			InputProcessor<ButtonClickEvent> buttonClickProcessor, MessageChannel msg, User target) {
		am.create(msg.sendMessageEmbeds(embed()), target, reactionProcessor, buttonClickProcessor);
	}

	public void send(Clover clover, MessageChannel msg, User target) {
		am.create(clover, msg.sendMessageEmbeds(embed()), target);
	}
}
