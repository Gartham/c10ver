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
import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;
import gartham.c10ver.economy.users.EconomyUser;
import gartham.c10ver.utils.Utilities;

public class ExploreCommand extends MatchBasedCommand {

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

	public ExploreCommand(Clover clover) {
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
				handle(inv, true);
				return;
			}
		}
		handle(inv, false);
	}

	private void handle(CommandInvocation inv, boolean safari) {
		if (Math.random() < 0.3) {

			int rand = (int) (Math.random() * 5);
			String msg;
			RewardsOperation op;
			EconomyUser user = clover.getEconomy().getUser(inv.event.getAuthor().getId());

			switch (rand) {
			case 0:
				var cloves = Utilities.randBIFromMean(32, 15);
				msg = inv.event.getAuthor().getAsMention() + ", you found " + Utilities.format(cloves)
						+ " hidden in a bush.";
				op = RewardsOperation.build(user, inv.event.getGuild(), cloves);
				break;
			case 1:
				cloves = Utilities.randBIFromMean(75, 15);
				msg = inv.event.getAuthor().getAsMention() + ", you found a wallet in the woods with "
						+ Utilities.format(cloves) + " in it.";
				op = RewardsOperation.build(user, inv.event.getGuild(), cloves);
				break;
			case 2:
				cloves = Utilities.randBIFromMean(82, 15);
				msg = inv.event.getAuthor().getAsMention() + ", you find a cave with a chest in it full of "
						+ Utilities.format(cloves) + '.';
				op = RewardsOperation.build(user, inv.event.getGuild(), cloves);
				break;
			case 3:
				cloves = Utilities.randBIFromMean(64, 15);
				msg = inv.event.getAuthor().getAsMention() + ", you stole " + Utilities.format(cloves)
						+ " from an unsuspecting mailbox.";
				op = RewardsOperation.build(user, inv.event.getGuild(), cloves);
				break;
			case 4:
				cloves = Utilities.randBIFromMean(73, 15);
				msg = inv.event.getAuthor().getAsMention()
						+ ", you purchased a sandwich but used so many coupons that the store paid you "
						+ Utilities.format(cloves) + " back.";
				op = RewardsOperation.build(user, inv.event.getGuild(), cloves, new ItemBunch<>(new Sandwich()));
				break;

			default:
				assert false : "Random value not handled by switch.";
				msg = null;
				op = null;
			}

			assert msg != null && op != null : "Values were assigned null by switch.";

			inv.event.getChannel().sendMessage(msg + "\n\n" + Utilities.listRewards(user.reward(op))).queue();

		} else {

		}
	}

}
