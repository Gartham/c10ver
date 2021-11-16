package gartham.c10ver.games.rpg;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.CommandInvocation;
import gartham.c10ver.commands.MatchBasedCommand;
import gartham.c10ver.utils.Utilities;

public class GARPGCommand extends MatchBasedCommand {

	private static final long EXPLORE_COMMAND_SEPARATION_LATENCY = 3000;// 3000 millis before command can be reinvoked.

	private static final class State {
		private Instant ts;
		private boolean reminded;

		public State(Instant ts, boolean reminded) {
			this.ts = ts;
			this.reminded = reminded;
		}

	}

	private final Timer timer = new Timer(true);
	{
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				try {
					Instant thresh = Instant.now().minusMillis(EXPLORE_COMMAND_SEPARATION_LATENCY);
					for (Iterator<Entry<String, State>> iterator = useTimestamps.entrySet().iterator(); iterator
							.hasNext();)
						// Clear all timestamps that are at least 3000 seconds in the past.
						if (thresh.isAfter(iterator.next().getValue().ts))
							iterator.remove();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 3600000, 3600000);
	}

	private final Clover clover;
	private final Map<String, State> useTimestamps = new HashMap<>();

	public GARPGCommand(Clover clover) {
		super("explore", "rpg");
		this.clover = clover;
	}

	/**
	 * Checks the current timestamp and the timestamp of the last successful
	 * invocation. If {@link #EXPLORE_COMMAND_SEPARATION_LATENCY} milliseconds have
	 * not passed between the two timestamps, returns <code>false</code>. Otherwise,
	 * returns <code>true</code> after storing the current timestamp as the
	 * timestamp of the last successful invocation. This is performed per-user.
	 * 
	 * @param inv The invocation.
	 * @return <code>true</code> if enough time has passed since the user has
	 *         invoked this command.
	 */
	private Duration processTimestamps(CommandInvocation inv) {
		if (useTimestamps.containsKey(inv.event.getAuthor().getId())) {
			Instant threshold = Instant.now().minusMillis(EXPLORE_COMMAND_SEPARATION_LATENCY),
					prevUseTime = useTimestamps.get(inv.event.getAuthor().getId()).ts;
			if (!prevUseTime.isBefore(threshold))
				return Duration.between(threshold, prevUseTime);
		}
		useTimestamps.put(inv.event.getAuthor().getId(), new State(Instant.now(), false));
		return null;
	}

	@Override
	public void exec(CommandInvocation inv) {

		{
			Duration d = processTimestamps(inv);
			if (d != null) {
				State state = useTimestamps.get(inv.event.getAuthor().getId());
				if (!state.reminded) {
					inv.event.getChannel().sendMessage(
							"You have to wait **" + Utilities.formatLargest(d, 1) + "** before you can explore again!")
							.queue();
					state.reminded = true;
				}
				return;
			}
		}

		if (inv.event.isFromGuild() && clover.getEconomy().hasServer(inv.event.getGuild().getId())) {
			var serv = clover.getEconomy().getServer(inv.event.getGuild().getId());
			if (inv.event.getChannel().getId().equals(serv.getRPGChannel())) {
				handleSafariInvocation(inv);
				return;
			}
		}
		handleGeneralInvocation(inv);
	}

	private void handleSafariInvocation(CommandInvocation inv) {
		// TODO Handle command being used in the rpg channel.
	}

	private void handleGeneralInvocation(CommandInvocation inv) {
		// TODO Handle command being used in non-rpg channel.
	}

}
