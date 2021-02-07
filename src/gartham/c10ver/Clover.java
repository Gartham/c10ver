package gartham.c10ver;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;

public class Clover {
	public static void main(String[] args) throws LoginException {
		try (var s = new Scanner(Clover.class.getResourceAsStream("token.txt"))) {
			var jda = JDABuilder.createLight(s.nextLine()).build();
			jda.addEventListener(new CommandHandler());
		}
	}
}
