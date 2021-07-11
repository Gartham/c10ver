package gartham.c10ver.games.rpg.fighting;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.alixia.javalibrary.JavaTools;

public class Team implements Iterable<Fighter> {
	private final List<Fighter> members;

	public Team(List<Fighter> members) {
		this.members = members;
	}

	/**
	 * Returns a read-only view of the members of this {@link Team}.
	 * 
	 * @return {@link Collections#unmodifiableList(List)} of the members of this
	 *         {@link Team}.
	 */
	public List<Fighter> viewMembers() {
		return Collections.unmodifiableList(members);
	}

	@Override
	public ListIterator<Fighter> iterator() {
		return JavaTools.unmodifyingListIterator(members.listIterator());
	}

}
