package gartham.c10ver.games.rpg.fighting;

public interface Fighter extends Comparable<Fighter> {
	int speed();

	int health();

	@Override
	default int compareTo(Fighter o) {
		return speed() - o.speed();
	}
}
