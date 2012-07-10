package ui;

import java.util.Date;

import common.properties.PropertiesManager;

public class Display {

	private static boolean debug = Boolean.parseBoolean(PropertiesManager.getLocalProperty("Debug"));

	public static void write(String line, Object... args) {
		String formattedLine = line;
		if (args != null && args.length > 0)
			formattedLine = String.format(line, args);
		System.out.printf("%s: %s\n", new Date().toString(), formattedLine);
	}

	public static void log(String line) {
		Date d = new Date();
		System.out.println(d.toString() + ": " + line);
	}

	public static void rawLog(String line) {
		System.out.println(line);
	}

	public static void debug (String line, Object... args) {
		String formattedLine = line;
		if (args != null && args.length > 0)
			formattedLine = String.format(line, args);
		debug(formattedLine);
	}
	
	public static void debug(String line) {
		if (debug) {
			Date d = new Date();
			String debugLine = "[DEBUG]" + d.toString() + ": " + line;
			System.out.println(debugLine);
		}
	}
}
