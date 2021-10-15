package gartham.c10ver.response.menus;

import gartham.c10ver.Clover;
import gartham.c10ver.response.actions.Action;
import gartham.c10ver.response.actions.ActionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public abstract class MenuMessage<A extends Action, AM extends ActionMessage<A>> {

	protected abstract void buildEmbed(EmbedBuilder builder);

	private final AM am;

	public AM getAm() {
		return am;
	}

	private MenuMessage(AM am) {
		this.am = am;
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
