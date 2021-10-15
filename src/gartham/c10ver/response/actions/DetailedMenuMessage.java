package gartham.c10ver.response.actions;

import java.util.Iterator;

import gartham.c10ver.response.menus.MenuMessage;
import net.dv8tion.jda.api.EmbedBuilder;

public class DetailedMenuMessage<D extends DetailedAction> extends MenuMessage<D> {

	@SafeVarargs
	public DetailedMenuMessage(D... actions) {
		super(new ActionMessage<>(actions));
	}

	public DetailedMenuMessage(Iterable<? extends D> actions) {
		super(new ActionMessage<>(actions));
	}

	public DetailedMenuMessage(Iterator<? extends D> actions) {
		super(new ActionMessage<>(actions));
	}

	@Override
	protected void buildEmbed(EmbedBuilder builder) {
		int i = 0;
		for (D d : getAm().getActions()) {
			builder.addField(
					(d.getEmoji() == null ? ActionMessage.getNumericEmoji(i) : d.getEmoji()) + ' ' + d.getName(),
					d.getDetails(), true);
			i++;
		}
	}

}
