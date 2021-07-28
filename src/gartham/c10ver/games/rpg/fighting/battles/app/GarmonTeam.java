package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.Collection;

import gartham.c10ver.games.rpg.fighting.battles.api.Team;

public class GarmonTeam extends Team<GarmonFighter> {
	private final String name;

	public GarmonTeam(String name, Collection<? extends GarmonFighter> members) {
		super(members);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public GarmonTeam(String name, GarmonFighter... members) {
		super(members);
		this.name = name;
	}

	public GarmonTeam(String name, Iterable<? extends GarmonFighter> members) {
		super(members);
		this.name = name;
	}

}
