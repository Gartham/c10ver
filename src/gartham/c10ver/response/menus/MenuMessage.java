package gartham.c10ver.response.menus;

import java.util.Iterator;
import java.util.List;

import gartham.c10ver.Clover;
import gartham.c10ver.response.actions.Action;
import gartham.c10ver.response.actions.ActionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 * The {@link MenuMessage} class lets you send {@link ActionMessage}s (messages
 * that have emojis or buttons at the bottom which do something when clicked)
 * through a consistent format. A menu message not only adds the emojis/buttons
 * to the message after/before it's sent, a {@link MenuMessage} will actually
 * <i>build the message</i>, based off of the {@link Action}s the message will
 * let the user select. Essentially this class lets you present the user with an
 * {@link ActionMessage} but this class will also build a menu in the message,
 * possibly with descriptions, named entries, etc.
 * 
 * @author Gartham
 *
 * @param <A> The type of {@link Action} (typically corresponds to the type of
 *            {@link MenuMessage}).
 */
public abstract class MenuMessage<A extends Action> {

	protected abstract void buildEmbed(EmbedBuilder builder);

	private final ActionMessage<A> am;

	public ActionMessage<A> getAm() {
		return am;
	}

	public final List<A> getActions() {
		return am.getActions();
	}

	public MenuMessage(ActionMessage<A> am) {
		this.am = am;
	}

	@SafeVarargs
	public MenuMessage(A... actions) {
		this(new ActionMessage<>(actions));
	}

	public MenuMessage(Iterable<A> actions) {
		this(new ActionMessage<>(actions));
	}

	public MenuMessage(Iterator<A> actions) {
		this(new ActionMessage<>(actions));
	}

	public final MessageEmbed embed() {
		var e = new EmbedBuilder();
		buildEmbed(e);
		return e.build();
	}

	public void send(Clover clover, MessageChannel msg, User target) {
		am.create(clover, msg.sendMessage(embed()), target);
	}
}
