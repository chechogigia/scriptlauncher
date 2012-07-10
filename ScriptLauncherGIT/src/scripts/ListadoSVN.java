/* 
 * 1. Identificacion:
 *    Fichero        : ListadoSVN.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 12/08/2011
 *
 * 2. Proposito:
 *    This script lists the files that have been inserted/modified within the selected task.
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   12/08/2011 S.Ruiz   Primera implementacion.
 */
package scripts;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import common.util.FileUtil;
import common.util.LocalUtil;

import scripts.common.SVNBasedScript;
import ui.CustomFileChooser;
import ui.Display;

public class ListadoSVN extends SVNBasedScript {

	/**
	 * Name of the script.
	 */
	public static final String SCRIPTNAME = "Listar ficheros de una incidencia";
	
	/**
	 * Execute the script.<br/>
	 * This script lists the files that have been inserted/modified within the selected task.
	 */
	@Override
	public void exec() {
		List<String> lista = getFileListForSelectedTask();

		if (lista == null || lista.isEmpty()) {
			//Display.debug("No hay tarea seleccionada.");
			return;
		}

		Collections.sort(lista);

		String exportList = "";
		String relativePath;
		for (String file : lista) {
			relativePath = LocalUtil.getRelativePath(file);
			Display.write(relativePath);
			exportList += relativePath + "\n";
		}

		CustomFileChooser jfc = new CustomFileChooser(LocalUtil.getCurrentPackageDirectory(), "Seleccion del fichero de salida");
		if (jfc.showSaveDialog(null) == CustomFileChooser.APPROVE_OPTION) {
			try {
				FileUtil.writeFile(jfc.getSelectedFile(), exportList);
			} catch (IOException e) {
				Display.debug(e.getMessage());
			}
		}
	}

	/**
	 * Returns the name of the script
	 * 
	 * @return Name of the script
	 */
	@Override
	public String getName() {
		return SCRIPTNAME;
	}
}
