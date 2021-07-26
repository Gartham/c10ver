package gartham.c10ver.games.rpg.fighting.battles.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;
import gartham.c10ver.games.rpg.fighting.fighters.SimpleFighter;

public class Team implements Iterable<SimpleFighter> {
	private final Set<SimpleFighter> members;

	public Team(Collection<? extends SimpleFighter> members) {
		if (members.isEmpty())
			throw new IllegalArgumentException();
		for (Fighter f : members)
			if (f == null)
				throw null;
		this.members = new HashSet<>(members);
	}

	public Team(SimpleFighter... members) {
		if (members.length == 0)
			throw new IllegalArgumentException();
		this.members = new HashSet<>();
		for (SimpleFighter f : members)
			if (f == null)
				throw null;
			else
				this.members.add(f);
	}

	public Team(Iterable<? extends SimpleFighter> members) {
		this.members = new HashSet<>();
		for (SimpleFighter f : members)
			if (f == null)
				throw null;
			else
				this.members.add(f);
		if (this.members.isEmpty())
			throw new IllegalArgumentException();
	}

	/**
	 * Removes the specified {@link SimpleFighter} from this {@link Team}'s {@link Set} of
	 * {@link SimpleFighter}s.
	 * 
	 * @param fighter The {@link SimpleFighter} to remove.
	 */
	void remove(Fighter fighter) {
		members.remove(fighter);
	}

	/**
	 * Adds the specified {@link SimpleFighter} to this {@link Team}.
	 * 
	 * @param fighter The {@link SimpleFighter} to add.
	 */
	void add(SimpleFighter fighter) {
		members.add(fighter);
	}

	/**
	 * Returns a read-only view of the members of this {@link Team}. The view is in
	 * no particular order.
	 * 
	 * @return {@link Collections#unmodifiableList(List)} of the members of this
	 *         {@link Team}.
	 */
	public Set<SimpleFighter> viewMembers() {
		return Collections.unmodifiableSet(members);
	}

	@Override
	public Iterator<SimpleFighter> iterator() {
		return JavaTools.unmodifyingIterator(members.iterator());
	}

}
