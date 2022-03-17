package gartham.c10ver;

public class CloverConfiguration {

	public final boolean devmode, disableTransactionHandler;
	/**
	 * <p>
	 * The token passed in through the command line, if any. This field is not final
	 * so that it may be nulled by Clover, if needed in case there is suspicion of
	 * potential for arbitrary code execution by third parties. This field may
	 * therefore be <code>null</code> even if a token was provided through the
	 * command line.
	 * </p>
	 */
	public String commandLineToken;

	public CloverConfiguration(String[] args) {
		boolean devmode = false, disableTransactionHandler = false;
		for (var s : args)
			switch (s.toLowerCase()) {
			case "dev":
				devmode = true;
				break;
			case "dth":
				disableTransactionHandler = true;
				break;
			default:
				if (s.startsWith("clt="))
					commandLineToken = s.substring(4);
			}

		this.devmode = devmode;
		this.disableTransactionHandler = disableTransactionHandler;
	}
}
