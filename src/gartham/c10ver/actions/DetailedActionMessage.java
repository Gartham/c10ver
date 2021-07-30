package gartham.c10ver.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class DetailedActionMessage<D extends DetailedAction> extends ActionMessage<D> {

	private final List<D> actions;

	@SafeVarargs
	public DetailedActionMessage(D... actions) {
		this.actions = new ArrayList<>();
		for (var d : actions)
			this.actions.add(d);
	}

	public DetailedActionMessage(Collection<D> actions) {
		this.actions = new ArrayList<>(actions);
	}

	public DetailedActionMessage(Iterator<D> actions) {
		this.actions = new ArrayList<>();
		while (actions.hasNext())
			this.actions.add(actions.next());
	}

	@Override
	public MessageEmbed embed() {
		var emb = new EmbedBuilder();
		int i = 0;
		for (D d : actions) {
			emb.addField((d.getEmoji() == null ? ActionMessage.getNumericEmoji(i) : d.getEmoji()) + ' ' + d.getName(),
					d.getDetails(), true);
			i++;
		}
		return emb.build();
	}

}
