/* 
 * 1. Identificacion:
 *    Fichero        : OptionManager.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 20/12/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ----------- ------------------------------------------
 *    1.0   -			S.Ruiz   	Primera implementacion.
 *    2.0	21/12/2011	S.Ruiz		Cambio de diseño e implementacion
 *    3.0   15/06/2012  S.Ruiz      Use of ScriptWrapper object
 */
package launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ui.Display;
import ui.SeleccionEntorno;

import common.properties.DefaultProperties;
import common.properties.PropertiesManager;
import common.util.ScriptWrapper;

/**
 * The new version of the scripts only uses a reduced set of parameters of the
 * previous version. This is because now the packages directory is set in the
 * Properties file and the rfc selected to work with is chosen in runtime
 * instead of using as a parameter
 * 
 * @author sruizabad
 * 
 */
public class OptionManager {

	private static final String DefaultPropertiesName = "Properties.txt";
	
	/*************** FIELDS SECTION ***************/

	/**
	 * 
	 */
	public String[] args = null;

	/**
	 * 
	 */
	private ScriptWrapper scriptWrapper;

	/**
	 * 
	 */
	private String environment;

	/**
	 * 
	 */
	private List<String> otherArgs;

	/**
	 * 
	 */
	private String filePropertiesPath;

	
	/*************** CONSTANTS SECTION ***************/

	/**
	 * All OK
	 */
	public static final int OK = 0;

	/**
	 * No arguments available
	 */
	public static final int NO_ARGS = 1;

	/**
	 * No script name available
	 */
	public static final int NO_SCRIPTNAME = 2;

	/**
	 * 
	 */
	public static final int NORCMODE_NOCOMMANDS = 3;

	/**
	 * No properties file path available
	 */
	public static final int COMMON_NO_PROPERTIES_FILE = 4;

	
	/*************** CONSTRUCTORS SECTION ***************/

	/**
	 * Creates a new instance of OptionManager
	 * @param args
	 */
	public OptionManager(String[] args) {
		this.args = args;
	}
	
	
	/*************** METHODS SECTION ***************/

	/**
	 * Checks the arguments of the script
	 * 
	 * @return {@link #OK} if all is right or
	 *         {@link #NO_ARGS}, 
	 *         {@link #NO_SCRIPTNAME},
	 *         {@link #NORCMODE_NOCOMMANDS},
	 *         {@link #COMMON_NO_PROPERTIES_FILE} if not
	 */
	public int checkArgs() {
		if (args.length == 0) {
			File propertiesFile = new File ("./".concat(DefaultPropertiesName));
			if (propertiesFile == null || !propertiesFile.exists() || !propertiesFile.isFile() || !propertiesFile.canRead())
				return COMMON_NO_PROPERTIES_FILE;
			filePropertiesPath = propertiesFile.getPath();
			return OK;
		}

		otherArgs = new ArrayList<String>();

		String scriptName = null;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-p") && (i + 1 < args.length)) { //$NON-NLS-1$
				filePropertiesPath = args[++i];
				continue;
			}
			if (args[i].equals("-e") && (i + 1 < args.length)) {//$NON-NLS-1$
				environment = args[++i];
				continue;
			}
			if (args[i].startsWith("-")) {
				otherArgs.add(args[i].substring(1));
				continue;
			}

			scriptName = args[i];
		}

		if (filePropertiesPath == null) {
			File propertiesFile = new File ("./".concat(DefaultPropertiesName));
			if (propertiesFile == null || !propertiesFile.exists() || !propertiesFile.isFile() || !propertiesFile.canRead())
				return COMMON_NO_PROPERTIES_FILE;
			filePropertiesPath = propertiesFile.getPath();
		}

		if (scriptName != null) {
			try {
				scriptWrapper = ScriptWrapper.createFromScriptName(scriptName);
			} catch (Exception e) {
				Display.write("Error al intentar acceder al script %s", e.getMessage());
			}
		}

		return OK;
	}

	/**
	 * 
	 * @return the ScriptWrapper
	 */
	public ScriptWrapper getScriptWrapper() {
		return scriptWrapper;
	}
	
	/**
	 * 
	 * @param sw
	 */
	public void setScriptWrapper (ScriptWrapper sw) {
		scriptWrapper = sw;
	}

	/**
	 * 
	 * @return the Environment
	 */
	public String getEnvironment() {
		if (environment == null) {
			SeleccionEntorno gui = new SeleccionEntorno(PropertiesManager.getAvailableEnvironments());
			if (gui.isCanceled())
				environment = DefaultProperties.getDefaultEnv();
			environment = PropertiesManager.getIdForEnvironmentName(gui.getSelectedValue());
			gui.dispose();
		}
		return environment;
	}

	/**
	 * 
	 * @return other Args
	 */
	public List<String> getOtherArgs() {
		return otherArgs;
	}

	/**
	 * 
	 * @return the property file path
	 */
	public String getPropertiesFile() {
		return filePropertiesPath;
	}
}