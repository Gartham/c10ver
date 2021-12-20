package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.Collection;

import gartham.c10ver.games.rpg.fighting.battles.api.Controller;
import gartham.c10ver.games.rpg.fighting.battles.api.Team;

public class GarmonTeam extends Team<GarmonFighter> {
	private final String name;
	private final Controller<GarmonFighter> controller;

	public GarmonTeam(Iterable<? extends GarmonFighter> members, String name, Controller<GarmonFighter> controller) {
		super(members);
		this.name = name;
		this.controller = controller;
	}

	public GarmonTeam(String name, Controller<GarmonFighter> controller, GarmonFighter... members) {
		super(members);
		this.name = name;
		this.controller = controller;
	}

	public GarmonTeam(Collection<? extends GarmonFighter> members, String name, Controller<GarmonFighter> controller) {
		super(members);
		this.name = name;
		this.controller = controller;
	}

	public String getName() {
		return name;
	}

	public Controller<GarmonFighter> getController() {
		return controller;
	}

}
