package gartham.utilities.c10ver.logo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Class designed to randomize the logo file used to generate the bot logo
 * (<code>logo.bat</code> in the root of the project).
 * 
 * @author Gartham
 *
 */
public class LogoRandomizer {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
		System.out.print("Enter the logo batch file to be randomized: ");
		var s = new Scanner(System.in);
		File f = new File(s.nextLine());
		var fs = new Scanner(f);
		StringBuilder sb = new StringBuilder();
		while (fs.hasNextLine()) {
			var str = fs.nextLine();
			if (str.toLowerCase().startsWith("echo.")) {
				byte[] b = str.getBytes(StandardCharsets.UTF_8);
				for (int i = 0; i < b.length; i++)
					if (b[i] == '0' || b[i] == '1') {
						b[i] = (byte) (Math.random() < 0.5 ? '0' : '1');
					}
				str = new String(b, StandardCharsets.UTF_8);
			}
			sb.append(str + '\n');
		}

		fs.close();
		PrintWriter pw = new PrintWriter(f);
		pw.print(sb.toString());
		pw.flush();

		System.out.println("Completed!");
	}
}
