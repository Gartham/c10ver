package gartham.c10ver.economy.items;

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.alixia.javalibrary.json.JSONObject;

/**
 * A {@link UserInventory} is an {@link Inventory} that has an associated
 * {@link File directory} denoting where the Inventory is/should be saved to.
 * This directory is used to automatically save the {@link Inventory} upon
 * necessary changes to its state.
 * 
 * @author Gartham
 *
 */
public class UserInventory extends Inventory {
	private final File invdir;

	@Override
	public <I extends Item> UserEntry<I> add(I item) {
		return (UserEntry<I>) super.add(item);
	}

	@Override
	public <I extends Item> UserEntry<I> add(I item, BigInteger amt) {
		return (UserEntry<I>) super.add(item, amt);
	}

	@Override
	public UserEntry<?> get(int index) {
		return (UserEntry<?>) super.get(index);
	}

	@Override
	public UserEntry<?> get(String key) {
		return (UserEntry<?>) super.get(key);
	}

	public UserEntry<?> get(Item item) {
		return (UserEntry<?>) super.get(item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I extends Item> UserEntry<I> add(ItemBunch<? extends I> items) {
		return (UserEntry<I>) super.add(items);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<? extends UserEntry<?>> add(ItemBunch<?>... items) {
		return (Set<? extends UserEntry<?>>) super.add(items);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends UserEntry<?>> getEntries() {
		return (List<? extends UserEntry<?>>) super.getEntries();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends UserEntry<?>> getPage(int page, int pagesize) {
		return (List<? extends UserEntry<?>>) super.getPage(page, pagesize);
	}

	@Override
	protected <I extends Item> UserEntry<I> newEntry(File file) {
		return new UserEntry<>(file);
	}

	@Override
	protected <I extends Item> UserEntry<I> newEntry(I item, BigInteger amount) {
		return new UserEntry<>(item, amount);
	}

	public UserInventory(File userDir) {
		invdir = new File(userDir, "inventory");
		load(invdir);
	}

	public void load() {
		load(invdir);
	}

	public void saveAll() {
		saveAll(invdir);
	}

	public class UserEntry<I extends Item> extends Entry<I> {

		public UserEntry(File f) {
			super(f);
		}

		public UserEntry(I item, BigInteger amt) {
			super(item, amt);
			save();
		}

		public class UserItemStack extends ItemStack {

			public UserItemStack(I item, BigInteger amount) {
				super(item, amount);
			}

			public UserItemStack(JSONObject json) {
				super(json);
			}

			@SuppressWarnings("unchecked")
			@Override
			public UserItemStack remove(BigInteger amt) {
				return (UserItemStack) super.remove(amt);
			}

			public void save() {
				saveInto(invdir);
			}

			public void removeAndSave(BigInteger amt) {
				var is = remove(amt);
				if (is != null)
					is.save();
			}

		}

		public File getFile() {
			return getFile(invdir);
		}

		@SuppressWarnings("unchecked")
		@Override
		public UserItemStack get(I item) {
			return (UserItemStack) super.get(item);
		}

		@SuppressWarnings("unchecked")
		@Override
		public UserItemStack get(int index) {
			return (UserItemStack) super.get(index);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<? extends UserItemStack> getPage(int page, int pagesize) {
			return (List<? extends UserItemStack>) super.getPage(page, pagesize);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<? extends UserItemStack> getStacks() {
			return (List<? extends UserItemStack>) super.getStacks();
		}

		@Override
		protected UserItemStack newItemStack(I item, BigInteger amt) {
			return new UserItemStack(item, amt);
		}

		@Override
		protected UserItemStack newItemStack(JSONObject json) {
			return new UserItemStack(json);
		}

		@Override
		public boolean remove(I item, BigInteger amt) {
			var file = getFile();// Can't be called after removal.
			boolean res = super.remove(item, amt);
			if (stacks.isEmpty())
				file.delete();
			return res;
		}

		@Override
		protected void remove(Entry<I>.ItemStack is) {
			super.remove(is);
			getFile().delete();
		}

		/**
		 * Calls {@link Entry#saveInto(File)} with {@link UserInventory#invdir} as the
		 * argument.
		 */
		public void save() {
			super.saveInto(invdir);
		}

	}

}
