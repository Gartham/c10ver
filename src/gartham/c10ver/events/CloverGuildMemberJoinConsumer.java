package gartham.c10ver.events;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.economy.Multiplier;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class CloverGuildMemberJoinConsumer implements InputConsumer<GuildMemberJoinEvent> {

	private final Clover clover;

	public CloverGuildMemberJoinConsumer(Clover clover) {
		this.clover = clover;
	}

	@Override
	public boolean consume(GuildMemberJoinEvent event, InputProcessor<? extends GuildMemberJoinEvent> processor,
			InputConsumer<GuildMemberJoinEvent> consumer) {
		synchronized (this) {
			var ge = (GuildMemberJoinEvent) event;
			Invite inviteee = clover.getInviteTracker().calcUser(ge);
			if (inviteee == null)
				return false;

			var u = inviteee.getInviter();
			if (u == null || u.isBot())
				return false;

			var inviter = clover.getEconomy().getAccount(u.getId());
			var joinee = clover.getEconomy().getUser(ge.getUser().getId());

			var serv = clover.getEconomy().getServer(ge.getGuild().getId());
			if (serv.getIgnoredInvites().contains(inviteee.getCode())) {
				if (joinee.getJoinedGuilds().contains(ge.getGuild().getId())) {
					print(joinee.getUser().getAsTag() + '[' + joinee.getUserID() + "] joined " + ge.getGuild().getName()
							+ '[' + ge.getGuild().getId() + "] with an ignored invite, again.");
					if (clover.getEconomy().hasServer(ge.getGuild().getId())) {
						var g = clover.getEconomy().getServer(ge.getGuild().getId());
						if (g.getGeneralChannel() != null) {
							var gen = ge.getGuild().getTextChannelById(g.getGeneralChannel());
							if (gen != null)
								gen.sendMessage(ge.getUser().getAsMention() + " welcome back to the server. ^w^")
										.queue();
						}
					}
				} else {
					print(joinee.getUser().getAsTag() + '[' + joinee.getUserID() + "] joined " + ge.getGuild().getName()
							+ '[' + ge.getGuild().getId() + "] with an ignored invite, for the FIRST time.");
					Multiplier mult = Multiplier.ofHr(3, BigDecimal.ONE);
					joinee.addMultiplier(mult);
					joinee.getJoinedGuilds().add(ge.getGuild().getId());
					joinee.save();

					if (clover.getEconomy().hasServer(ge.getGuild().getId())) {
						var g = clover.getEconomy().getServer(ge.getGuild().getId());
						if (g.getGeneralChannel() != null) {
							var gen = ge.getGuild().getTextChannelById(g.getGeneralChannel());
							if (gen != null)
								gen.sendMessage(ge.getUser().getAsMention()
										+ " welcome to the server. ^w^\nYou received a multiplier of "
										+ Utilities.prettyPrintMultiplier(BigDecimal.ONE) + " that lasts for **3h**.")
										.queue();
						}
					}
				}
			} else {
				StringBuilder sb;
				var inv = inviter.getUser().getUser();
				var join = joinee.getUser();
				sb = new StringBuilder(inv == null ? "#Deleted Acc" : inv.getAsTag());
				sb.append('[').append(inv == null ? "#DelUser" : inv.getId()).append("] has invited ")
						.append(join.getAsTag()).append('[').append(join.getId()).append("] to ")
						.append(ge.getGuild().getName()).append('[').append(ge.getGuild().getId()).append(']');
				if (joinee.getJoinedGuilds().contains(ge.getGuild().getId())) {
					print(sb.append('.').toString());

					if (clover.getEconomy().hasServer(ge.getGuild().getId())) {
						var g = clover.getEconomy().getServer(ge.getGuild().getId());
						if (g.getGeneralChannel() != null) {
							var gen = ge.getGuild().getTextChannelById(g.getGeneralChannel());
							if (gen != null)
								gen.sendMessage(ge.getUser().getAsMention()
										+ " welcome back to the server.\nYou were invited back by: "
										+ (inv == null ? "a deleted user" : inv.getAsMention()) + ".").queue();
						}
					}
				} else {
					print(sb.append(" for the FIRST time.").toString());

					Multiplier mult = Multiplier.ofHr(3, BigDecimal.ONE);
					inviter.getUser().addMultiplier(mult);
					inviter.getUser().save();
					joinee.addMultiplier(mult);
					joinee.getJoinedGuilds().add(ge.getGuild().getId());
					joinee.save();

					if (clover.getEconomy().hasServer(ge.getGuild().getId())) {
						var g = clover.getEconomy().getServer(ge.getGuild().getId());
						if (g.getGeneralChannel() != null) {
							var gen = ge.getGuild().getTextChannelById(g.getGeneralChannel());
							if (gen != null)
								gen.sendMessage(ge.getUser().getAsMention() + " welcome to the server. \nYou and "
										+ inviter.getUser().getUser().getAsMention() + " both got a "
										+ Utilities.prettyPrintMultiplier(BigDecimal.ONE)
										+ " multiplier that lasts for **3h**.").queue();
						}
					}
				}

			}
		}
		return false;
	}

	private static void print(String str) {
		File file = new File("data/logs/invites.txt");
		file.getParentFile().mkdirs();
		try (var pw = new PrintWriter(new FileOutputStream(file, true))) {
			pw.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
