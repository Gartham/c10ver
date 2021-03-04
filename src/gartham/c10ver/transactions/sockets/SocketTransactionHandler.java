package gartham.c10ver.transactions.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.streams.CharacterStream;

import gartham.c10ver.transactions.Transaction;
import gartham.c10ver.transactions.TransactionHandler;

public class SocketTransactionHandler extends TransactionHandler {

	private ServerSocket connlistener;
	private int port;
	private volatile boolean running;

	public SocketTransactionHandler(int port) {
		this.port = port;
	}

	private void handleConreq(Socket socket) {
		try (InputStream is = socket.getInputStream()) {
			JSONValue parse = new JSONParser()
					.parse(CharacterStream.from(new InputStreamReader(is, StandardCharsets.UTF_8)));
			try {
				handleTransaction(new Transaction(parse));
			} catch (Exception e) {
				System.err.println(DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now())
						+ "An error occurred while handling a parsed transaction. Transaction contents: " + parse);
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println(
					DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now()) + "Failed to handle a transaction.");
			e.printStackTrace();
		}
	}

	/**
	 * Creates a {@link ServerSocket} on the loopback address and listens to an
	 * incoming connection on it. If an error occurs while waiting for an incoming
	 * connection and the {@link #running} variable is <code>true</code>, this
	 * method will print the error, reopen the {@link ServerSocket}, and listen to
	 * the connection again.
	 */
	private void makeServerAndListen() {
		while (running) {
			try {
				connlistener = new ServerSocket();
				connlistener.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
			} catch (IOException e) {
				if (running) {
					e.printStackTrace();
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					continue;
				} else
					return;
			}
			try {
				while (running) {
					Socket s = connlistener.accept();
					var t = new Thread(() -> handleConreq(s));
//					t.setDaemon(true);
					// We probly don't want a daemon thread for a transaction.
					t.start();
				}
			} catch (IOException e) {
				if (running) {
					e.printStackTrace();
					// TODO Provide the ability to register handlers.
					// TODO Notify handlers of events like this.
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					continue;// Relaunch the ServerSock and relisten.
				} else
					return;
			}
		}
	}

	/**
	 * Starts this {@link SocketTransactionHandler}. This attempts to open a
	 * {@link ServerSocket} on this {@link SocketTransactionHandler}'s {@link #port}
	 */
	@Override
	protected void enable() {
		if (connlistener != null)
			throw new RuntimeException("Already enabled.");
		running = true;
		Thread t = new Thread(this::makeServerAndListen);
		t.setDaemon(true);
		t.start();
	}

	@Override
	protected void destroy() {
		running = false;
		if (connlistener != null)
			try {
				connlistener.close();
			} catch (IOException e) {
			}
	}

}
