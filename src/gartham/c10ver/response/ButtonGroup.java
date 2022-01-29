package gartham.c10ver.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.alixia.javalibrary.JavaTools;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class ButtonGroup {
	private final List<MutableButton> buttons = new ArrayList<>();

	public List<MutableButton> getButtons() {
		return buttons;
	}

	public int size() {
		return buttons.size();
	}

	public boolean isEmpty() {
		return buttons.isEmpty();
	}

	public boolean contains(Object o) {
		return buttons.contains(o);
	}

	public Iterator<MutableButton> iterator() {
		return buttons.iterator();
	}

	public boolean add(MutableButton e) {
		return buttons.add(e);
	}

	public boolean remove(Object o) {
		return buttons.remove(o);
	}

	public boolean addAll(Collection<? extends MutableButton> c) {
		return buttons.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends MutableButton> c) {
		return buttons.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return buttons.removeAll(c);
	}

	public void clear() {
		buttons.clear();
	}

	public MutableButton get(int index) {
		return buttons.get(index);
	}

	public MutableButton set(int index, MutableButton element) {
		return buttons.set(index, element);
	}

	public void add(int index, MutableButton element) {
		buttons.add(index, element);
	}

	public MutableButton remove(int index) {
		return buttons.remove(index);
	}

	public int indexOf(Object o) {
		return buttons.indexOf(o);
	}

	public List<ActionRow> actionRows() {
		return actionRows(JavaTools.mask(buttons, MutableButton::getButton));
	}

	public static List<ActionRow> actionRows(Collection<? extends Button> actions) {
		if (actions.size() > 25)
			throw new IllegalStateException("Can't have more than 25 buttons on a MessageActionHandler at once.");
		List<ActionRow> ar = new ArrayList<>(actions.size() / 5);
		int i = 0;
		var itr = actions.iterator();
		List<Button> b = new ArrayList<>(5);
		while (itr.hasNext()) {
			var a = itr.next();
			if (a == null) {
				if (!b.isEmpty()) {
					i = 0;
					ar.add(ActionRow.of(b));
					b = new ArrayList<>(5);
				}
				continue;
			}
			b.add(a);
			if (++i == 5) {
				i = 0;
				ar.add(ActionRow.of(b));
				b = new ArrayList<>(5);
			}
		}
		if (!b.isEmpty())
			ar.add(ActionRow.of(b));

		return ar;
	}

	public static List<ActionRow> actionRows(Iterable<? extends Button> actions) {
		List<ActionRow> ar = new ArrayList<>(5);
		int i = 0;
		var itr = actions.iterator();
		List<Button> b = new ArrayList<>(5);
		while (itr.hasNext()) {
			var a = itr.next();
			if (a == null) {
				if (!b.isEmpty()) {
					i = 0;
					ar.add(ActionRow.of(b));
					b = new ArrayList<>(5);
				}
				continue;
			}
			b.add(a);
			if (++i == 5) {
				i = 0;
				ar.add(ActionRow.of(b));
				b = new ArrayList<>(5);
			}
		}
		if (!b.isEmpty())
			ar.add(ActionRow.of(b));

		return ar;
	}

}
