package gartham.c10ver.events;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.economy.Server;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

public class CloverGuildMemberAddRoleConsumer implements InputConsumer<GuildMemberRoleAddEvent> {

	private final Clover clover;

	public CloverGuildMemberAddRoleConsumer(Clover clover) {
		this.clover = clover;
	}

	@Override
	public boolean consume(GuildMemberRoleAddEvent event, InputProcessor<? extends GuildMemberRoleAddEvent> processor,
			InputConsumer<GuildMemberRoleAddEvent> consumer) {

		var e = (GuildMemberRoleAddEvent) event;
		Server s = clover.getEconomy().getServer(e.getGuild().getId());
		var role = s.getVoteRole();
		if (role != null)
			for (Role r : e.getRoles())
				if (r.getId().equals(role)) {
					try {
						e.getGuild().removeRoleFromMember(e.getMember(), r).queue();
					} catch (Exception er) {
						System.err.println("An error occurred while attempting to remove the vote role from a user!");
						er.printStackTrace();
					}
					clover.getVoteManager().handleVoteRoleAdded(e.getMember());
					break;
				}

		return false;
	}

}
