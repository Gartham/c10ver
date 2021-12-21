package gartham.c10ver.games.rpg.fighting.battles.api;

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public interface Controller<F extends Fighter> {

	int move(F fighter);

}
