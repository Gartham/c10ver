package gartham.c10ver.response.menus;

import gartham.c10ver.response.actions.ActionMessage;
import gartham.c10ver.response.actions.NamedActionButton;
import gartham.c10ver.response.actions.NamedActionReaction;
import net.dv8tion.jda.api.EmbedBuilder;

public class NameMenuMessage<R extends NamedActionReaction, B extends NamedActionButton> extends MenuMessage<R, B> {

	private static final String[] EMOJIS = ActionMessage.emojis();

	public NameMenuMessage(ActionMessage<R, B> am) {
		super(am);
	}

	@Override
	protected void buildEmbed(EmbedBuilder builder) {
		for (int i = 0; i < getButtons().size(); i++)
			builder.appendDescription(
					getButtons().get(i).getEmoji() == null ? EMOJIS[i] : getButtons().get(i).getEmoji())
					.appendDescription(" ").appendDescription(getButtons().get(i).getName()).appendDescription("\n");
	}

}
