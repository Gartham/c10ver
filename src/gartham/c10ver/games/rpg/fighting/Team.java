package gartham.c10ver.games.rpg.fighting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alixia.javalibrary.JavaTools;

public class Team implements Iterable<Fighter> {
	private final Set<Fighter> members;

	public Team(Collection<? extends Fighter> members) {
		this.members = new HashSet<>(members);
		for (var f : members) {
			if (f.getTeam() != null)
				f.getTeam().remove(f);
			f.setTeam(this);
		}
	}

	/**
	 * Removes the specified {@link Fighter} from this {@link Team}'s {@link Set} of
	 * {@link Fighter}s.
	 * 
	 * @param fighter The {@link Fighter} to remove.
	 */
	void remove(Fighter fighter) {
		members.remove(fighter);
	}

	/**
	 * Returns a read-only view of the members of this {@link Team}.
	 * 
	 * @return {@link Collections#unmodifiableList(List)} of the members of this
	 *         {@link Team}.
	 */
	public Set<Fighter> viewMembers() {
		return Collections.unmodifiableSet(members);
	}

	@Override
	public Iterator<Fighter> iterator() {
		return JavaTools.unmodifyingIterator(members.iterator());
	}

}
