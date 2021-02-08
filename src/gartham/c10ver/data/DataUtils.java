package gartham.c10ver.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.alixia.javalibrary.json.JSONObject;
import org.alixia.javalibrary.json.JSONParser;
import org.alixia.javalibrary.json.JSONValue;
import org.alixia.javalibrary.streams.CharacterStream;

public class DataUtils {
	public static JSONObject loadObj(File file) {
		return (JSONObject) load(file);
	}

	public static JSONValue load(File file) {
		if (!file.isFile())
			return null;
		try (var isr = new InputStreamReader(new FileInputStream(file))) {
			return new JSONParser().parse(CharacterStream.from(isr));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void save(JSONValue obj, File file) {
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
			try (var pw = new PrintWriter(file)) {
				pw.println(obj.toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
