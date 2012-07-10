package strings;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import common.properties.DefaultProperties;

public class Messages {
	private static final String BUNDLE_NAME = "strings.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, DefaultProperties.getLocale());

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
