package gartham.c10ver.economy;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.server.ColorRole;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Server extends SavablePropertyObject {
	// TODO Specify mapProperty's partial immutability.
	private final Property<Map<String, ColorRole>> colorRoles = mapProperty("color-roles", Map.of(),
			toObjectGateway(ColorRole::new));
	private final Property<HashSet<String>> ignoredInvites = setProperty("ignored-invites", new HashSet<>(),
			JSON_STRING_GATEWAY);
	private final Property<String> generalChannel = stringProperty("general-channel"),
			spamChannel = stringProperty("spam-channel"), gamblingChannel = stringProperty("gambling-channel"),
			voteChannel = stringProperty("vote-channel"),
			privateChannelCategory = stringProperty("private-channel-category"),
			loggingChannel = stringProperty("logging-channel"), rpgChannel = stringProperty("rpg-channel");
	private final Property<String> voteRole = stringProperty("vote-role");
	private final Property<ArrayList<Multiplier>> multipliers = listProperty("multipliers",
			toObjectGateway(Multiplier::new));
	private final String serverID;

	public String getServerID() {
		return serverID;
	}

	public String getVoteRole() {
		return voteRole.get();
	}

	public String getVoteChannel() {
		return voteChannel.get();
	}

	public void setVoteRole(String role) {
		voteRole.set(role);
	}

	public void setRPGChannel(String rpgChannel) {
		this.rpgChannel.set(rpgChannel);
	}

	public String getRPGChannel() {
		return rpgChannel.get();
	}

	/**
	 * Returns the total server multiplier that this server has active. If there are
	 * no active multipliers, this method returns <code>1.0</code>, (<b>not</b>
	 * <code>0.0</code>).
	 * 
	 * @return The total server multiplier, i.e.
	 */
	public BigDecimal getTotalServerMultiplier() {
		return MultiplierManager.getTotalValue(multipliers.get());
	}

	public List<Multiplier> listMultipliers() {
		return MultiplierManager.getMultipliers(multipliers.get());
	}

	public void addMultiplier(Multiplier multiplier) {
		MultiplierManager.addMultiplier(multiplier, multipliers.get());
	}

	public Map<String, ColorRole> getColorRoles() {
		return colorRoles.get();
	}

	public void setColorRoles(Map<String, ColorRole> colorRoles) {
		this.colorRoles.set(colorRoles);
	}

	public String getGeneralChannel() {
		return generalChannel.get();
	}

	public void setGeneralChannel(String channelID) {
		generalChannel.set(channelID);
	}

	public void setVoteChannel(String channelID) {
		voteChannel.set(channelID);
	}

	public String getSpamChannel() {
		return spamChannel.get();
	}

	public String getGamblingChannel() {
		return gamblingChannel.get();
	}

	public void setSpamChannel(String channelID) {
		spamChannel.set(channelID);
	}

	public void setGamblingChannel(String channelID) {
		gamblingChannel.set(channelID);
	}

	public void setIgnoredInvites(HashSet<String> values) {
		ignoredInvites.set(values);
	}

	public HashSet<String> getIgnoredInvites() {
		return ignoredInvites.get();
	}

	public Property<HashSet<String>> ignoredInvitesProperty() {
		return ignoredInvites;
	}

	public boolean isGeneral(MessageChannel mc) {
		return mc != null && mc.getId().equals(getGeneralChannel());
	}

	public boolean isGambling(MessageChannel mc) {
		return mc != null && mc.getId().equals(getGamblingChannel());
	}

	public boolean isSpam(MessageChannel mc) {
		return mc != null && mc.getId().equals(getSpamChannel());
	}

	public Server(File saveLocation) {
		this(saveLocation, true);
	}

	public Server(File saveLocation, boolean load) {
		super(new File(saveLocation, "server-data.txt"));
		serverID = saveLocation.getName();
		if (load)
			load();
		if (multipliers.get() == null)
			multipliers.set(new ArrayList<>());
	}

	public String getPCCategory() {
		return privateChannelCategory.get();
	}

	public void setPCCategory(String pcc) {
		privateChannelCategory.set(pcc);
	}

	public String getLoggingChannel() {
		return loggingChannel.get();
	}

	public void setLoggingChannel(String channel) {
		loggingChannel.set(channel);
	}

}
