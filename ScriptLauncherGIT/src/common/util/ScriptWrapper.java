/* 
 * 1. Identificacion:
 *    Fichero        : ScriptWrapper.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 14/06/2012
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   14/06/2012 S.Ruiz   Primera implementacion.
 */
package common.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


import scripts.common.Script;
import strings.Messages;
import ui.Display;
import ui.SeleccionGenerica;

import common.classes.ClassFilenameFilter;
import common.classes.ClassUtil;
import common.properties.DefaultProperties;

public class ScriptWrapper {

	/**
	 * File where the item is stored (class)
	 */
	File file = null;

	/**
	 * The class of the item
	 */
	Class<?> scriptClass = null;

	public static ScriptWrapper createFromScriptName(String name) throws URISyntaxException {
		File dir = new File(Script.class.getResource("../").toURI());
		if (!dir.isDirectory() || !dir.canRead())
			return null;

		File scriptFile = new File(dir, name.concat(".class"));
		if (!scriptFile.isFile() || !scriptFile.canRead())
			return null;

		return createFromLocalFile(scriptFile);
	}

	public static ScriptWrapper createFromLocalFile(File file) {
		ScriptWrapper sw = new ScriptWrapper();
		sw.file = file;
		File parentDir = file.getParentFile();
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".");
		String scriptName = null;
		if (index > 0)
			scriptName = fileName.substring(0, index);

		try {
			sw.scriptClass = ClassUtil.getClassFromDirectory(scriptName, parentDir);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return sw;
	}

	public static List<ScriptWrapper> createFromLocalDir(File dir) {
		List<File> files = FileUtil.getFilesForDirectory(dir, new ClassFilenameFilter());
		List<ScriptWrapper> wrappers = new ArrayList<ScriptWrapper>();
		for (File file : files) {
			wrappers.add(createFromLocalFile(file));
		}
		return wrappers;
	}

	public Script getInstanceOfScript() throws InstantiationException, IllegalAccessException, Exception {
		if (scriptClass == null)
			throw new Exception("No hay script cargado");
		Object instance = scriptClass.newInstance();
		if (!Script.class.isInstance(instance))
			throw new Exception("No es instancia de Script");
		return (Script) instance;
	}

	public static ScriptWrapper selectScript() {
		ScriptWrapper returnValue = null;

		String scriptDirPath = DefaultProperties.getScriptsLocation();
		if (scriptDirPath == null || scriptDirPath.isEmpty()) {
			Display.write("No se ha indicado el directorio de scripts en el fichero Properties.txt");
			return null;
		}
		
		File scriptsDir = new File (scriptDirPath);
		if(!scriptsDir.exists() || !scriptsDir.isDirectory() || !scriptsDir.canRead()) {
			Display.write("No se puede acceder al directorio indicado en el fichero Properties.txt (%s) revise que existe y que tiene permisos de lectura", scriptDirPath);
			return null;
		}
		
		SeleccionGenerica gui = new SeleccionGenerica(ScriptWrapper.createFromLocalDir(scriptsDir), Messages.getString("Launcher.101")); //$NON-NLS-1$ //$NON-NLS-2$
		if (!gui.isCanceled())
			returnValue = (ScriptWrapper) gui.getListSelectedValue();
		gui.dispose();
		return returnValue;
	}

	@Override
	public String toString() {
		if (scriptClass == null)
			return "Error al cargar la clase del Script";
		return ClassUtil.invokeMethod(scriptClass, "getName");
	}
}
