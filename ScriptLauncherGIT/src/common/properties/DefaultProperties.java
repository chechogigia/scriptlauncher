package common.properties;

import java.util.Locale;

public class DefaultProperties {

	private static Locale locale = null;

	public final static String Redmine = "Redmine";

	public final static String SVN = "SVN";

	public final static String Local = "Local";

	private final static String SourceReader = "SourceReader";
	private final static String ListOutputFileName = "ListOutputFilename";
	private final static String PackagesPath = "PathPackages";
	private final static String CurrentPackage = "CurrentPackage";
	private final static String DefaultEnvironment = "DefaultEnv";
	
	private final static String ScriptsLocation = "ScriptsLocation";

	private final static String Locale = "Locale";

	public static Class<?> getSourceReader() {
		return PropertiesManager.getClassProperty(Local, SourceReader);
	}

	public static String getListOutputFileName() {
		return PropertiesManager.getLocalProperty(ListOutputFileName);
	}

	public static String getPackagesPath() {
		return PropertiesManager.getLocalProperty(PackagesPath);
	}

	public static String getCurrentPackage() {
		return PropertiesManager.getLocalProperty(CurrentPackage);
	}

	public static String getDefaultEnv() {
		return PropertiesManager.getLocalProperty(DefaultEnvironment);
	}
	
	public static String getScriptsLocation () {
		return PropertiesManager.getLocalProperty(ScriptsLocation);
	}

	public static Locale getLocale() {
		if (locale == null) {
			String localeProp = PropertiesManager.getLocalProperty(Locale);
			locale = (localeProp != null && !localeProp.isEmpty()) ? new Locale(localeProp) : new Locale("es_ES");
		}
		return locale;
	}
}
