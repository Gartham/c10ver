package gartham.c10ver.response.menus;

import gartham.c10ver.response.actions.ActionMessage;
import gartham.c10ver.response.actions.DetailedActionButton;
import gartham.c10ver.response.actions.DetailedActionReaction;
import net.dv8tion.jda.api.EmbedBuilder;

public class DetailedMenuMessage<R extends DetailedActionReaction, B extends DetailedActionButton>
		extends MenuMessage<R, B> {

	public DetailedMenuMessage(ActionMessage<R, B> am) {
		super(am);
	}

	@Override
	protected void buildEmbed(EmbedBuilder builder) {
		int i = 0;
		for (B d : getAm().getButtons()) {
			builder.addField(
					(d.getEmoji() == null ? ActionMessage.getNumericEmoji(i) : d.getEmoji()) + ' ' + d.getName(),
					d.getDetails(), true);
			i++;
		}
		for (R d : getAm().getReactions()) {
			builder.addField(
					(d.getEmoji() == null ? ActionMessage.getNumericEmoji(i) : d.getEmoji()) + ' ' + d.getName(),
					d.getDetails(), true);
			i++;
		}
	}

}
