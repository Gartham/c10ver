package gartham.c10ver.actions;

import java.util.Iterator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
	public MessageEmbed embed() {
		var emb = new EmbedBuilder();
		int i = 0;
		for (D d : getActions()) {
			emb.addField((d.getEmoji() == null ? ActionMessage.getNumericEmoji(i) : d.getEmoji()) + ' ' + d.getName(),
					d.getDetails(), true);
			i++;
		}
		return emb.build();
	}

}
