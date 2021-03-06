package gartham.c10ver.games.rpg.fighting.battles.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alixia.javalibrary.JavaTools;

import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

public class Team<F extends Fighter> implements Iterable<F> {
	private final Set<F> members;

	public Team(Collection<? extends F> members) {
		if (members.isEmpty())
			throw new IllegalArgumentException();
		for (F f : members)
			if (f == null)
				throw null;
		this.members = new HashSet<>(members);
	}

	@SafeVarargs
	public Team(F... members) {
		if (members.length == 0)
			throw new IllegalArgumentException();
		this.members = new HashSet<>();
		for (F f : members)
			if (f == null)
				throw null;
			else
				this.members.add(f);
	}

	public Team(Iterable<? extends F> members) {
		this.members = new HashSet<>();
		for (F f : members)
			if (f == null)
				throw null;
			else
				this.members.add(f);
		if (this.members.isEmpty())
			throw new IllegalArgumentException();
	}

	public boolean contains(Fighter f) {
		return members.contains(f);
	}

	/**
	 * Returns a read-only view of the members of this {@link Team}. The view is in
	 * no particular order.
	 * 
	 * @return {@link Collections#unmodifiableList(List)} of the members of this
	 *         {@link Team}.
	 */
	public Set<F> memberView() {
		return Collections.unmodifiableSet(members);
	}

	@Override
	public Iterator<F> iterator() {
		return JavaTools.unmodifyingIterator(members.iterator());
	}

}
