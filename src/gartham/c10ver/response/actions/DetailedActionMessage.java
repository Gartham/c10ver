package gartham.c10ver.response.actions;

import java.util.Iterator;

import net.dv8tion.jda.api.EmbedBuilder;

public class DetailedActionMessage<D extends DetailedAction> extends ActionMessage<D> {

	@SafeVarargs
	public DetailedActionMessage(D... actions) {
		super(actions);
	}

	public DetailedActionMessage(Iterable<D> actions) {
		super(actions);
	}

	public DetailedActionMessage(Iterator<D> actions) {
		super(actions);
	}

	@Override
	protected void buildEmbed(EmbedBuilder builder) {
		int i = 0;
		for (D d : getActions()) {
			builder.addField(
					(d.getEmoji() == null ? ActionMessage.getNumericEmoji(i) : d.getEmoji()) + ' ' + d.getName(),
					d.getDetails(), true);
			i++;
		}
	}

}
