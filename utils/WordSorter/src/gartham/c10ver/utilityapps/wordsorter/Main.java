package gartham.c10ver.utilityapps.wordsorter;

import java.io.File;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		var s = new Scanner(System.in);
		while (s.hasNextLine()) {
			sort(new File(s.nextLine()));
		}
	}

	private static void sort(File f) {

	}
}
