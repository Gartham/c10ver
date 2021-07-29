package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.function.Consumer;

import gartham.c10ver.actions.Action;
import gartham.c10ver.actions.ActionInvocation;

public class GarmonAction extends Action {

	private final String description;

	public GarmonAction(String emoji, String name, String description, Consumer<ActionInvocation> action) {
		super(emoji, name, action);
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
