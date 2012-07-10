package common;

import com.jcraft.jsch.Logger;

import java.util.Hashtable;

import ui.Display;

public class CustomLogger implements Logger {
	static Hashtable<Integer, String> name = new Hashtable<Integer, String>();
	static {
		name.put(new Integer(DEBUG), "DEBUG: ");
		name.put(new Integer(INFO), "INFO: ");
		name.put(new Integer(WARN), "WARN: ");
		name.put(new Integer(ERROR), "ERROR: ");
		name.put(new Integer(FATAL), "FATAL: ");
	}

	public boolean isEnabled(int level) {
		if ((level == ERROR) || (level == FATAL))
			return true;
		return false;
	}

	public void log(int level, String message) {
		Display.log(name.get(new Integer(level)) + " " + message);
	}
}