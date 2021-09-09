package gartham.c10ver.economy.privatechannels;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.alixia.javalibrary.util.StringGateway;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.Owned;
import gartham.c10ver.economy.users.User;

public class PrivateChannel extends SavablePropertyObject implements Owned<User> {
	private final Property<String> channel = stringProperty("channel");
	private final Property<HashSet<String>> users = setProperty("users", toStringGateway(StringGateway.string()));

	public Set<String> getUsers() {
		return users.get();
	}

	public String getChannel() {
		return channel.get();
	}

	private final User owner;

	@Override
	public User getOwner() {
		return owner;
	}

	public PrivateChannel(File file, User owner, boolean load) {
		super(file);
		this.owner = owner;
		if (load)
			load();
		if (users.get() == null)
			users.set(new HashSet<>());
	}

	public long cost() {
		return 25000 + 5000 * users.get().size();
	}

	@Override
	public String toString() {
		return "<#" + channel.get() + ">";
	}

}