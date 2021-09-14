package gartham.c10ver.economy.privatechannels;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.alixia.javalibrary.util.StringGateway;

import gartham.c10ver.Clover;
import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.Owned;
import gartham.c10ver.economy.users.User;
import net.dv8tion.jda.api.entities.TextChannel;

public class PrivateChannel extends SavablePropertyObject implements Owned<User> {
	private final Property<String> channel = stringProperty("channel"), owner = stringProperty("owner");
	private final Property<HashSet<String>> users = setProperty("users", toStringGateway(StringGateway.string()));
	private final Clover clover;

	public Set<String> getUsers() {
		return users.get();
	}

	public String getChannel() {
		return channel.get();
	}

	public TextChannel getDiscordChannel() {
		return clover.getBot().getTextChannelById(getChannel());
	}

	@Override
	public User getOwner() {
		return clover.getEconomy().getUser(owner.get());
	}

	private PrivateChannel(File file, Clover clover) {
		super(file);
		this.clover = clover;
		load();
		if (users.get() == null)
			users.set(new HashSet<>());
	}

	public static final PrivateChannel load(File file, Clover clover) {
		return new PrivateChannel(file, clover);
	}

	public PrivateChannel(File file, Clover clover, String channel, String owner) {
		super(file);
		this.clover = clover;
		users.set(new HashSet<>());
		this.channel.set(channel);
		this.owner.set(owner);
	}

	public void setOwnerID(String owner) {
		this.owner.set(owner);
	}

	public String getOwnerID() {
		return owner.get();
	}

	public long cost() {
		return 25000 + 5000 * users.get().size();
	}

	@Override
	public String toString() {
		return "<#" + channel.get() + ">";
	}

}