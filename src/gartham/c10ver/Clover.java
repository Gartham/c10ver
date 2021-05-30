package gartham.c10ver;

import static gartham.c10ver.events.InfoPopup.tip;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import org.alixia.javalibrary.strings.matching.Matching;

import gartham.c10ver.changelog.Changelog;
import gartham.c10ver.commands.CommandParser;
import gartham.c10ver.commands.CommandProcessor;
import gartham.c10ver.economy.Economy;
import gartham.c10ver.events.EventHandler;
import gartham.c10ver.events.InfoPopup;
import gartham.c10ver.transactions.Transaction;
import gartham.c10ver.transactions.Transaction.Entry;
import gartham.c10ver.transactions.TransactionHandler;
import gartham.c10ver.transactions.sockets.SocketTransactionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Clover {
	{

		COMP_BLOCK: {
			InputStream stream = EventHandler.class.getResourceAsStream("/tips.txt");
			LOAD_BLOCK: if (stream != null) {
				List<InfoPopup> tl = new ArrayList<>(5);
				try (var s = new Scanner(stream)) {
					while (s.hasNextLine())
						tl.add(tip(s.nextLine()));
				} catch (Exception e) {
					e.printStackTrace();
					break LOAD_BLOCK;
				}
				tiplist = tl;
				break COMP_BLOCK;
			}
			tiplist = List.of(tip(
					"You can get daily, weekly, and monthly rewards with the commands: `~daily`, `~weekly`, and `~monthly` respectively!"),
					tip("Every time you send a message in #general, there's a small chance you'll stumble upon some loot."),
					tip("You can open crates using the `open crate` command! Just type `~open crate crate-type`."),
					tip("You can pay other users using the `pay` command!"),
					tip("Eating food will give you a temporary multiplier. You can eat food with `~use food-name`."));
		}

	}
	private final JDA bot;
	private final CommandParser commandParser;
	private final CommandProcessor commandProcessor = new CloverCommandProcessor(this);
	private final EventHandler eventHandler = new EventHandler(this);
	private final Economy economy = new Economy(new File("data/economy"), this);
	private final Changelog changelog;
	private final Set<String> devlist;
	private final List<String> wordlist;
	private final List<InfoPopup> tiplist;

	public List<InfoPopup> getTiplist() {
		return tiplist;
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

		Changelog changelog;
		try {
			changelog = Changelog.from(Clover.class.getResourceAsStream("changelog.txt"));
		} catch (Exception e) {
			System.err.println("FAILED TO LOAD THE CHANGELOG.");
			e.printStackTrace();
			changelog = null;
		}
		this.changelog = changelog;

		List<String> wordlist = new ArrayList<>();
		InputStream wl = Clover.class.getResourceAsStream("words/wordlist.txt");
		if (wl == null)
			System.err.println("Couldn't find the worldlist...");
		else
			try (var sc = new Scanner(wl)) {
				while (sc.hasNextLine())
					wordlist.add(sc.nextLine());
			}
		this.wordlist = Collections.unmodifiableList(wordlist);
	}

	public Changelog getChangelog() {
		return changelog;
	}

	public boolean hasLoadedChangelog() {
		return changelog != null;
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

	public CommandParser getCommandParser() {
		return commandParser;
	}

	public CommandProcessor getCommandProcessor() {
		return commandProcessor;
	}

	public EventHandler getEventHandler() {
		return eventHandler;
	}

	public Economy getEconomy() {
		return economy;
	}

	public Clover(String token) throws LoginException {
		this(JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class)).build());
	}

	public Clover(JDA jda) {
		bot = jda;
		commandParser = new CommandParser(Matching.build("~").or(
				Matching.build("<@").possibly("!").then(bot.getSelfUser().getId() + ">").then(Matching.whitespace())));
		bot.addEventListener(eventHandler);
		eventHandler.initialize();

		transactionHandler.enable();
	}

	public static void main(String[] args) throws LoginException {
		try (var s = new Scanner(Clover.class.getResourceAsStream("token.txt"))) {
			new Clover(s.nextLine());
		}
	}
}
