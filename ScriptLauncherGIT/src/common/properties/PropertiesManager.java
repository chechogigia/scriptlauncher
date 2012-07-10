/*****************************************************************************
 * Name:   PropertiesManager.java                                            *
 * Author: Sergio Ruiz                                                       *
 * Date:   14/10/2010                                                        *
 * --------------------------------------------------------------------------*
 * Descr.: This class is used to get whatever property you need from file    *
 *         Properties.txt                                                    *
 * --------------------------------------------------------------------------*
 * History:                                                                  *
 * 	1.0		14/10/10	S. Ruiz		Initial version			                 *			
 *****************************************************************************/
package common.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * How to use this class:<br/>
 * <br/>
 * <ul>
 * <li>only call method getProperty(String environment, String propertyName) with the environment and the property you want to get and the
 * code make all necessary steps to read property file and access the property.</li>
 * </ul>
 * 
 * @author Sergio Ruiz
 */
public class PropertiesManager {

	/**
	 * Name of the propety that store the current user
	 */
	public static final String UserNameKey = "UserName";
	
	private static String propertiesFilePath;

	/**
	 * Private local field for store the properties data
	 */
	private static Map<String, Properties> environments = null;

	/**
	 * Sets the path of properties file
	 * 
	 * @param path
	 */
	public static void setPropertiesFilePath(String path) {
		propertiesFilePath = path;
	}

	/**
	 * Get the property needed.
	 * 
	 * @param environment
	 * @param propertyName
	 * @return Property
	 */
	public static String getProperty(String environment, String propertyName) {
		Properties properties = getPropertiesForEnvironment(environment);
		String prop = properties.getProperty(propertyName);
		if (prop == null) {
			for (Object actualProp : properties.keySet()) {
				if (actualProp.equals(propertyName)) {
					prop = properties.getProperty(prop);
					break;
				}
			}
		}
		return prop;
	}

	public static String getLocalProperty(String propertyName) {
		return getProperty(DefaultProperties.Local, propertyName);
	}

	/**
	 * Set value for a property
	 * 
	 * @param environment
	 * @param propertyName
	 * @param value
	 */
	public static void setProperty(String environment, String propertyName, String value) {
		Properties properties = getPropertiesForEnvironment(environment);
		properties.put(propertyName, value);
	}

	/**
	 * Get the int property needed.<br/>
	 * 
	 * @param environment
	 * @param propertyName
	 * @param defaultValue
	 *            this value is returned if a NumberFormatExcepcion ocurred
	 * @return Integer property
	 */
	public static int getIntProperty(String environment, String propertyName, int defaultValue) {
		String property = getProperty(environment, propertyName);
		try {
			return Integer.parseInt(property);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * Get the class property needed.<br/>
	 * 
	 * @param environment
	 * @param propertyName
	 * @return Class property
	 */
	public static Class<?> getClassProperty(String environment, String propertyName) {
		String className = getProperty(environment, propertyName);

		if (className == null) {
			System.out.println("Parametro " + propertyName + " no encontrado");
			return null;
		}

		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.out.println("Error: clase " + className + " no disponible");
			return null;
		}
	}

	/**
	 * Get all properties for an environment.
	 * 
	 * @param environment
	 * @return Properties
	 */
	public static Properties getPropertiesForEnvironment(String environment) {
		Properties properties = getEnvironmentsMap().get(environment);
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	/**
	 * Get the map of properties initialized
	 * 
	 * @return Environment map
	 */
	private static Map<String, Properties> getEnvironmentsMap() {
		if (environments == null) {
			createEnvironmentsMap();
		}
		return environments;
	}

	/**
	 * 
	 * @param environmentName
	 * @return Id
	 */
	public static String getIdForEnvironmentName(String environmentName) {
		if (environmentName == null)
			return null;
		Map<String, Properties> environments = getEnvironmentsMap();
		Iterator<String> it = environments.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String nombre = getProperty(key, "Nombre");
			if (nombre != null && environmentName.equals(nombre))
				return key;
		}
		return null;
	}

	/**
	 * 
	 * @return Available environments
	 */
	public static List<String> getAvailableEnvironments() {
		List<String> names = new ArrayList<String>();
		Map<String, Properties> environments = getEnvironmentsMap();
		Iterator<String> it = environments.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!DefaultProperties.Local.equals(key)) {
				String nombre = getProperty(key, "Nombre");
				if (nombre != null && !names.contains(nombre))
					names.add(nombre);
			}
		}
		return names;
	}

	/**
	 * Create and initialized the map of properties
	 */
	private static void createEnvironmentsMap() {
		environments = Collections.synchronizedMap(new HashMap<String, Properties>());

		if (propertiesFilePath == null) {
			System.out.println("No properties file path specified");
			return;
		}

		// The file must exists and it can be readable
		File propertiesFile = new File(propertiesFilePath);
		if (!propertiesFile.exists() || !propertiesFile.canRead())
			return;

		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(propertiesFile);
			br = new BufferedReader(fr);

			// Temporal variables
			String linea;
			// Map<String, String> tempMap = new Hashtable<String, String>();
			Properties tempMap = new Properties();
			String actualEnvironment = null;

			// For each line readed
			while ((linea = br.readLine()) != null) {
				// if line is 'key=value' is a property
				if (linea.matches(".*=.*")) {
					String[] data = linea.split("=");
					tempMap.put(data[0].trim(), data[1].trim());
				}
				// is line is 'key' is an environment header
				else {
					// if there is another environment initialized, put temp data into map
					if (actualEnvironment != null) {
						environments.put(actualEnvironment, tempMap);
						// tempMap = new Hashtable<String, String>();
						tempMap = new Properties();
					}
					actualEnvironment = linea;
				}
			}

			// End of file, is there is an environment initialized, put temp data into map
			if (actualEnvironment != null) {
				environments.put(actualEnvironment, tempMap);
			}
		}

		// Manage a possible exception
		catch (Exception e) {
			e.printStackTrace();
		}

		// Close streams and file
		finally {
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	public static void checkProperties() {
		Map<String, Properties> envMap = getEnvironmentsMap();
		for (String env : envMap.keySet()) {
			System.out.println("Environment: " + env);
			Properties props = envMap.get(env);
			for (Object prop : props.keySet()) {
				System.out.println(" - " + prop + ": " + props.get(prop));
			}
		}
	}
}
