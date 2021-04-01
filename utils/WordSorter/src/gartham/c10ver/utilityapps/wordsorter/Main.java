package gartham.c10ver.utilityapps.wordsorter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		var s = new Scanner(System.in);
		while (s.hasNextLine()) {
			try {
				sort(new File(s.nextLine()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void sort(File f) throws IOException {
		var st = new Splittree<>();
		var sc = new Scanner(f);
		while (sc.hasNextLine())
			st.put(sc.nextLine(), true);
	}
}
