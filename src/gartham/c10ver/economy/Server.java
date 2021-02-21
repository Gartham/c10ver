package gartham.c10ver.economy;

import java.io.File;
import java.util.Map;

import gartham.c10ver.data.autosave.SavablePropertyObject;
import gartham.c10ver.economy.server.ColorRole;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Server extends SavablePropertyObject {
	// TODO Specify mapProperty's partial immutability.
	private final Property<Map<String, ColorRole>> colorRoles = mapProperty("color-roles", Map.of(),
			toObjectGateway(ColorRole::new));
	private final Property<String> generalChannel = stringProperty("general-channel"),
			spamChannel = stringProperty("spam-channel"), gamblingChannel = stringProperty("gambling-channel");
	private final String serverID;

	public String getServerID() {
		return serverID;
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
	}

}
