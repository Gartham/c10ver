package gartham.c10ver;

public class CloverConfiguration {

	private boolean devmode, disableTransactionHandler;

	public boolean isDevmode() {
		return devmode;
	}

	public boolean isDisableTransactionHandler() {
		return disableTransactionHandler;
	}

	public CloverConfiguration(String[] args) {
		for (var s : args)
			switch (s.toLowerCase()) {
			case "dev":
				devmode = true;
				break;
			case "dth":
				disableTransactionHandler = true;
			}
	}
}
