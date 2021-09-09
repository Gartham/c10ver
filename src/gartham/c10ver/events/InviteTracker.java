package gartham.c10ver.events;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.VanityInvite;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVanityCodeEvent;

public class InviteTracker {
	private final Map<String, Map<String, Integer>> invites = new HashMap<>();
	private final EventHandler eventHandler;

	public InviteTracker(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	public void initialize() {
		try {
			eventHandler.getClover().getBot().awaitReady();
		} catch (InterruptedException e) {
		}
		for (Guild g : eventHandler.getClover().getBot().getGuilds()) {
			Map<String, Integer> invmap = new HashMap<>();
			invites.put(g.getId(), invmap);
			for (var i : g.retrieveInvites().complete())
				invmap.put(i.getCode(), i.getUses());
			if (g.getSelfMember().hasPermission(Permission.ADMINISTRATOR)) {
				if (g.getVanityCode() != null) {
//					System.out.println("Getting Vanity Invite for " + g.getName());
					invmap.put(g.getVanityCode(), g.retrieveVanityInvite().complete().getUses());
				} else
					System.out.println(g.getName()
							+ " does not have a vanity code! Server excluded from vanity code invite tracking.");
			} else
				System.out.println("No manage server perms on " + g.getName() + " so vanity invite tracking disabled!");
		}

	}

	public void vanityUpdate(GuildUpdateVanityCodeEvent ev) {
		invites.get(ev.getGuild().getId()).remove(ev.getOldVanityCode());
		invites.get(ev.getGuild().getId()).put(ev.getNewVanityCode(), 0);
	}

	public void inviteCreated(GuildInviteCreateEvent ev) {
		try {
			System.out.println("Invite " + ev.getCode() + " created by: "
					+ ev.getInvite().expand().complete().getInviter().getAsMention());
			invites.get(ev.getGuild().getId()).put(ev.getInvite().expand().complete().getCode(),
					ev.getInvite().getUses());
		} catch (Exception e) {
			System.out.println("Error occurred while trying to store created invite.");
			e.printStackTrace();
		}
	}

	public void inviteDeleted(GuildInviteDeleteEvent ev) {
		try {
			System.out.println("Invite " + ev.getCode() + " from: " + ev);
			invites.get(ev.getGuild().getId()).remove(ev.getCode());
		} catch (Exception e) {
			System.out.println("Error occurred while trying to propagate deletion of invite.");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * @param ev The join event.
	 * @return Returns null if the invite is the vanity invite, otherwise, returns
	 *         the used invite.
	 */
	public Invite calcUser(GuildMemberJoinEvent ev) {
		var map = invites.get(ev.getGuild().getId());
		System.out.println(map + "\n");
		if (ev.getGuild().getVanityCode() != null) {
			VanityInvite vanityCode = ev.getGuild().retrieveVanityInvite().complete();
			if (map.get(ev.getGuild().getVanityCode()) != vanityCode.getUses()) {
				map.put(vanityCode.getCode(), vanityCode.getUses());
				return null;
			}
		}
		for (Iterator<Invite> iterator = ev.getGuild().retrieveInvites().complete().iterator(); iterator.hasNext();) {
			var i = iterator.next();
			if (i.getUses() != map.get(i.getCode())) {
				if (i.getMaxUses() != 0 && i.getMaxUses() <= map.get(i.getCode()))
					map.remove(i.getCode());
				else {
					System.out.println(ev.getUser().getAsTag() + " joined with invite: " + i.getCode() + ". It now has "
							+ i.getUses() + " uses.");
					map.put(i.getCode(), i.getUses());
				}
				while (iterator.hasNext()) {
					var j = iterator.next();
					if (j.isTemporary() && Duration.between(OffsetDateTime.now(), j.getTimeCreated()).getSeconds() > j
							.getMaxAge()) {
						System.out.println(
								"Found out that invite: " + j.getCode() + " expired due to time limit. Clearing.");
						map.remove(j.getCode());
					}
				}
				return i;
			}
			if (i.isTemporary()
					&& Duration.between(OffsetDateTime.now(), i.getTimeCreated()).getSeconds() > i.getMaxAge())
				map.remove(i.getCode());
		}

		System.out.println("All invites in cache had same use # as retrieved invites.");

		throw new RuntimeException("Something went wrong!");
	}

}
