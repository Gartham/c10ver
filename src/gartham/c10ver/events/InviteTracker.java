package gartham.c10ver.events;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

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
		System.out.println(eventHandler.getClover().getBot().getGuilds());
		for (Guild g : eventHandler.getClover().getBot().getGuilds()) {
			Map<String, Integer> invmap = new HashMap<>();
			invites.put(g.getId(), invmap);
			for (var i : g.retrieveInvites().complete()) {
				invmap.put(i.getCode(), i.getUses());
			}
		}
	}

	public void inviteCreated(GuildInviteCreateEvent ev) {
		invites.get(ev.getGuild().getId()).put(ev.getInvite().expand().complete().getCode(), ev.getInvite().getUses());
	}

	public void inviteDeleted(GuildInviteDeleteEvent ev) {
		invites.get(ev.getGuild().getId()).remove(ev.getCode());
	}

	public net.dv8tion.jda.api.entities.User calcUser(GuildMemberJoinEvent ev) {
		var map = invites.get(ev.getGuild().getId());
		for (Iterator<Invite> iterator = ev.getGuild().retrieveInvites().complete().iterator(); iterator.hasNext();) {
			var i = iterator.next();
			if (i.getUses() != map.get(i.getCode())) {
				if (i.getMaxUses() <= map.get(i.getCode())) {
					map.remove(i.getCode());
				} else
					map.put(i.getCode(), i.getUses());
				while (iterator.hasNext()) {
					var j = iterator.next();
					if (j.isTemporary() && Duration.between(OffsetDateTime.now(), j.getTimeCreated()).getSeconds() > j
							.getMaxAge()) {
						map.remove(j.getCode());
					}
				}
				return i.getInviter();
			}
			if (i.isTemporary()
					&& Duration.between(OffsetDateTime.now(), i.getTimeCreated()).getSeconds() > i.getMaxAge()) {
				map.remove(i.getCode());
			}
		}

		throw new RuntimeException("Something went wrong!");
	}

}
