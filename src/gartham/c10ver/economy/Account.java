package gartham.c10ver.economy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.streams.CharacterStream;

public class Account {
	private BigDecimal balance;

	private final File mainAccount;

	public Account(File f) throws IOException {
		mainAccount = new File(f, "main-account.txt");
		if (f.isDirectory()) {
			if (!mainAccount.isFile()) {
				System.err.println("User main account file doesn't exist, but dir does: " + f);
				balance = new BigDecimal(0);
			} else
				try (var isr = new InputStreamReader(new FileInputStream(mainAccount))) {
					JSONObject o = (JSONObject) new JSONParser().parse(CharacterStream.from(isr));
					String s = o.getString("bal");
					balance = new BigDecimal(s);
				}
		} else {
			f.mkdirs();
			if (!f.isDirectory())
				throw new IllegalArgumentException("Could not create directory for account: " + f);
			balance = new BigDecimal(0);

			mainAccount.createNewFile();
			try (PrintWriter pw = new PrintWriter(mainAccount)) {
				pw.println(toJSON().toString());
			}
		}
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void pay(BigDecimal amount, Account recipient) {
		// TODO Code.

	}

	public void pay(BigDecimal amount) throws RuntimeException {
		balance = balance.add(amount);
		try {
			try (PrintWriter pw = new PrintWriter(mainAccount)) {
				pw.println(toJSON().toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void pay(long amount) {
		pay(BigDecimal.valueOf(amount));
	}

	public JSONValue toJSON() {
		return new JSONObject().put("bal", balance.toString());
	}

}
