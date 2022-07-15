package gartham.c10ver;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import gartham.c10ver.commands.CommandParser;
import gartham.c10ver.economy.Economy;
import gartham.c10ver.events.CloverGuildMemberJoinConsumer;
import gartham.c10ver.events.CloverMessageConsumer;
import gartham.c10ver.events.EventHandler;
import gartham.c10ver.events.InviteTracker;
import gartham.c10ver.events.VoteManager;
import gartham.c10ver.transactions.Transaction;
import gartham.c10ver.transactions.Transaction.Entry;
import gartham.c10ver.transactions.TransactionHandler;
import gartham.c10ver.transactions.sockets.SocketTransactionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Clover {

	private final CloverConfiguration config;
	private final File root = new File("data");
	private final JDA bot;
	private final EventHandler eventHandler = new EventHandler();
	private final Economy economy = new Economy(new File(root, "economy"), this);
	private final InviteTracker inviteTracker = new InviteTracker(this);
	private final Set<String> devlist;
	private final VoteManager voteManager = new VoteManager(this);

	public InviteTracker getInviteTracker() {
		return inviteTracker;
	}

	public VoteManager getVoteManager() {
		return voteManager;
	}

	public CloverConfiguration getConfig() {
		return config;
	}

	/**
	 * Returns a new {@link File} representing the directory of the random storage
	 * namespace requested. Any part of Clover (particularly, parts that are not big
	 * enough to constitute their own, dedicated memory location), can access a
	 * "random-storage" namespace and store its data there. The random storage
	 * namespace is for random parts of Clover and is not different upon each
	 * program execution.
	 * 
	 * @param ns The namespace.
	 * @return A new file that the app can use to store data to.
	 */
	public File getRandStorage(String ns) {
		return new File(new File(root, "random-storage"), ns);
	}

	private final TransactionHandler transactionHandler = new SocketTransactionHandler(42000);
	{
		transactionHandler.setTransactionProcessor(new Consumer<Transaction>() {

			@Override
			public void accept(Transaction t) {
				try {
					bot.getUserById(t.getUserID()).openPrivateChannel().complete().sendMessage(
							"Thank you for purchasing multipliers on the Clover store. :heart:\n\nMultiplier tickets have been added to your inventory (check your inventory with `~inv`).\nYou can use them in any server you want to with `~use mult [item-number]`")
							.queue();
				} catch (Exception e) {
					System.err.println("Failed to msg a user about rewards.");
				}

				var i = economy.getInventory(t.getUserID());
				for (Entry e : t.getItems())
					i.add(e.getItem(), e.getAmt()).save();
			}
		});

		Set<String> devlist = new HashSet<>();
		InputStream dl = Clover.class.getResourceAsStream("devlist.txt");
		if (dl == null)
			System.err.println("Couldn't find the devlist...");
		else
			try (var s = new Scanner(dl)) {
				while (s.hasNextLine())
					devlist.add(s.nextLine());
			}
		this.devlist = Collections.unmodifiableSet(devlist);
	}

	public Set<String> getDevlist() {
		return devlist;
	}

	public boolean isDev(String id) {
		return devlist.contains(id);
	}

	public boolean isDev(long id) {
		return devlist.contains(Long.toString(id));
	}

	public boolean isDev(User user) {
		return devlist.contains(user.getId());
	}

	public JDA getBot() {
		return bot;
	}

	public EventHandler getEventHandler() {
		return eventHandler;
	}

	public Economy getEconomy() {
		return economy;
	}

	public Clover(String token, CloverConfiguration configuration) throws LoginException {
		this(JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class)).build(), configuration);
	}

	public Clover(JDA jda, CloverConfiguration configuration) {
		this(jda, configuration.devmode, configuration);
	}

	public Clover(String token, boolean devmode, CloverConfiguration configuration) throws LoginException {
		this(JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class)).build(), devmode, configuration);
	}

	public Clover(JDA jda, boolean devmode, CloverConfiguration configuration) {
		config = configuration == null ? new CloverConfiguration(new String[0]) : configuration;
		bot = jda;
		if (devmode)
			System.out.println("Dev mode enabled!");
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("Ping me for help!"));
		eventHandler.getProcessor(MessageReceivedEvent.class).registerInputConsumer(new CloverMessageConsumer(this));
		eventHandler.getProcessor(GuildMemberJoinEvent.class)
				.registerInputConsumer(new CloverGuildMemberJoinConsumer(this));
		eventHandler.getProcessor(GuildInviteCreateEvent.class).registerInputConsumer((event, processor, consumer) -> {
			inviteTracker.inviteCreated(event);
			return false;
		});
		eventHandler.getProcessor(GuildInviteDeleteEvent.class).registerInputConsumer((a, b, c) -> {
			inviteTracker.inviteDeleted(a);
			return false;
		});
		inviteTracker.initialize();

		if (!configuration.disableTransactionHandler)
			transactionHandler.enable();
		else
			System.out.println("Transaction handler disabled!");
	}

	public static void main(String[] args) throws LoginException {
		var ccfg = new CloverConfiguration(args);
		if (ccfg.commandLineToken == null) {
			String line;
			try (var s = new Scanner(Clover.class.getResourceAsStream(ccfg.devmode ? "dev-token.txt" : "token.txt"))) {
				line = s.nextLine();
			}
			new Clover(line, ccfg);
		} else
			new Clover(ccfg.commandLineToken, ccfg);
	}
}
