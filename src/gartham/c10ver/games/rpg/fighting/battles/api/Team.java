package gartham.c10ver.games.rpg.fighting.battles.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;
import gartham.c10ver.games.rpg.fighting.fighters.CustomFighter;

public class Team implements Iterable<CustomFighter> {
	private final Set<CustomFighter> members;

	public Team(Collection<? extends CustomFighter> members) {
		if (members.isEmpty())
			throw new IllegalArgumentException();
		for (Fighter f : members)
			if (f == null)
				throw null;
		this.members = new HashSet<>(members);
	}

	public Team(CustomFighter... members) {
		if (members.length == 0)
			throw new IllegalArgumentException();
		this.members = new HashSet<>();
		for (CustomFighter f : members)
			if (f == null)
				throw null;
			else
				this.members.add(f);
	}

	public Team(Iterable<? extends CustomFighter> members) {
		this.members = new HashSet<>();
		for (CustomFighter f : members)
			if (f == null)
				throw null;
			else
				this.members.add(f);
		if (this.members.isEmpty())
			throw new IllegalArgumentException();
	}

	/**
	 * Removes the specified {@link CustomFighter} from this {@link Team}'s {@link Set} of
	 * {@link CustomFighter}s.
	 * 
	 * @param fighter The {@link CustomFighter} to remove.
	 */
	void remove(Fighter fighter) {
		members.remove(fighter);
	}

	/**
	 * Adds the specified {@link CustomFighter} to this {@link Team}.
	 * 
	 * @param fighter The {@link CustomFighter} to add.
	 */
	void add(CustomFighter fighter) {
		members.add(fighter);
	}

	/**
	 * Returns a read-only view of the members of this {@link Team}. The view is in
	 * no particular order.
	 * 
	 * @return {@link Collections#unmodifiableList(List)} of the members of this
	 *         {@link Team}.
	 */
	public Set<CustomFighter> viewMembers() {
		return Collections.unmodifiableSet(members);
	}

	@Override
	public Iterator<CustomFighter> iterator() {
		return JavaTools.unmodifyingIterator(members.iterator());
	}

}
