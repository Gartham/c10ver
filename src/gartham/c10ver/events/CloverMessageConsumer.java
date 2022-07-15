package gartham.c10ver.events;

import static gartham.c10ver.utils.Utilities.format;
import static java.math.BigInteger.valueOf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import gartham.c10ver.Clover;
import gartham.c10ver.commands.InputProcessor;
import gartham.c10ver.commands.consumers.InputConsumer;
import gartham.c10ver.economy.RewardsOperation;
import gartham.c10ver.economy.items.ItemBunch;
import gartham.c10ver.economy.items.utility.crates.NormalCrate;
import gartham.c10ver.economy.items.utility.foodstuffs.Sandwich;
import gartham.c10ver.economy.users.EconomyUser;
import gartham.c10ver.utils.Utilities;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CloverMessageConsumer implements InputConsumer<MessageReceivedEvent> {

	private final Clover clover;

	public CloverMessageConsumer(Clover clover) {
		this.clover = clover;
	}

	@Override
	public boolean consume(MessageReceivedEvent event, InputProcessor<? extends MessageReceivedEvent> processor,
			InputConsumer<MessageReceivedEvent> consumer) {
		var mre = event;

		if (mre.isFromGuild() && clover.getEconomy().hasServer(mre.getGuild().getId())) {

			EconomyUser user = clover.getEconomy().getUser(mre.getAuthor().getId());
			user.incrementMessageCount();
			user.getMailbox().reward(RewardsOperation
					.build(user, mre.getGuild(),
							BigDecimal.valueOf(Math.random() * 4 + 2)
									.multiply(new BigDecimal(user.getPrestige().add(BigInteger.ONE))).toBigInteger())
					.setShouldSave(false));
			if (user.getMessageCount().getLowestSetBit() >= 4)// Save every 16 messages.
				user.getMailbox().saveCloves();
			if (!mre.getAuthor().isBot()) {
				BigInteger rewards = switch (user.getMessageCount().toString()) {
				case "10" -> valueOf(50);
				case "50" -> valueOf(100);
				case "100" -> valueOf(250);// TODO Handle spam channel.
				case "200" -> valueOf(300);
				case "250" -> valueOf(400);
				case "300" -> valueOf(450);
				case "400" -> valueOf(500);
				case "500" -> valueOf(750);
				case "750" -> valueOf(1000);
				case "1000" -> valueOf(1_500);
				case "2000" -> valueOf(2_500);
				case "2500" -> valueOf(3_000);
				case "3000" -> valueOf(3_000);
				case "4000" -> valueOf(5_000);
				case "5000" -> valueOf(10_000);
				case "10000" -> valueOf(25_000);
				case "15000" -> valueOf(40_000);
				case "25000" -> valueOf(50_000);
				case "50000" -> valueOf(100_000);
				case "75000" -> valueOf(100_000);
				case "100000" -> valueOf(100_000);
				case "250000" -> valueOf(500_000);
				case "500000" -> valueOf(1_000_000);
				case "1000000" -> valueOf(25_000_000);
				default -> null;
				};
				if (rewards != null)
					if (user.getSettings().isRandomRewardsNotifyingEnabled())
						mre.getChannel()
								.sendMessage(mre.getAuthor().getAsMention() + " congratulations, you just reached "
										+ user.getMessageCount() + " messages! You've earned: "
										+ Utilities.listRewards(
												user.reward(RewardsOperation.build(user, mre.getGuild(), rewards)))
										+ "\n Check your mailbox!")
								.queue(t -> t.delete().queueAfter(10, TimeUnit.SECONDS));
					else
						user.getMailbox().reward(RewardsOperation.build(user, mre.getGuild(), rewards));
				else {
					var serv = clover.getEconomy().getServer(mre.getGuild().getId());
					if (serv.isGeneral(mre.getChannel()) && Math.random() < 0.02) {
						RewardsOperation randrews = RewardsOperation.build(user, mre.getGuild(),
								BigInteger.valueOf((long) (Math.random() * 20 + 40)));

						if (user.getSettings().isRandomRewardsNotifyingEnabled()) {
							mre.getChannel()
									.sendMessage(mre.getAuthor().getAsMention()
											+ ", you found some cloves sitting on the ground.\n"
											+ Utilities.listRewards(user.reward(randrews)) + "\nTotal Cloves: "
											+ format(user.getAccount().getBalance()) + ". Check your mailbox!")
									.queue(t -> t.delete().queueAfter(10, TimeUnit.SECONDS));
						} else
							user.getMailbox().reward(randrews);
					} else if (serv.isGeneral(mre.getChannel()) && Math.random() < 0.01)
						if (Math.random() < 0.2) {
							NormalCrate crate = new NormalCrate();
							if (user.getSettings().isRandomRewardsNotifyingEnabled()) {
								user.getInventory().add(crate).save();
								mre.getChannel().sendMessage(mre.getAuthor().getAsMention()
										+ " you look hungry... for a loot crate! (Acquired `1`x " + crate.getIcon()
										+ crate.getEffectiveName() + ".)\nCheck your mailbox!")
										.queue(t -> t.delete().queueAfter(10, TimeUnit.SECONDS));
							} else
								user.getMailbox()
										.reward(RewardsOperation.build(user, mre.getGuild(), new ItemBunch<>(crate)));
						} else {
							BigInteger count = BigInteger.valueOf((long) (Math.random() * 3 + 1));
							Sandwich item = new Sandwich();
							if (user.getSettings().isRandomRewardsNotifyingEnabled()) {
								user.getInventory().add(item, count).save();
								mre.getChannel()
										.sendMessage(mre.getAuthor().getAsMention()
												+ " you look hungry. Have some sandwiches! (Acquired `" + count + "`x "
												+ item.getIcon() + item.getEffectiveName() + ".)\nCheck your mailbox!")
										.queue(t -> t.delete().queueAfter(10, TimeUnit.SECONDS));
							} else
								user.getMailbox().reward(
										RewardsOperation.build(user, mre.getGuild(), new ItemBunch<>(item, count)));

						}
				}
			}
		}
		return false;
	}
}
