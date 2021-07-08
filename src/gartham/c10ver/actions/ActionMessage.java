package gartham.c10ver.actions;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class ActionMessage {
	private final List<Action> actions = new ArrayList<>();

	public ActionMessage(Action... actions) {
		for (var a : actions)
			this.actions.add(a);
	}

	public List<Action> getActions() {
		return actions;
	}

	public abstract MessageEmbed embed();

}
