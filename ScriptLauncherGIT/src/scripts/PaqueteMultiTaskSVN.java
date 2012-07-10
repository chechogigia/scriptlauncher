/* 
 * 1. Identificacion:
 *    Fichero        : PaqueteMultiTaskSVN.java
 *    Autor          : R. Lanza
 *    Version        : 1.0
 *    Fecha          : 06/03/2012
 *
 * 2. Proposito:
 *    Genera una copia de los ficheros de las incidencias seleccionadas.
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   06/03/2012 R.Lanza   Primera implementacion.
 */
package scripts;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import common.util.FileUtil;
import common.util.LocalUtil;
import common.util.SVNUtil;

import ui.CustomFileChooser;
import ui.Display;

import scripts.common.MultiSVNBasedScript;

public class PaqueteMultiTaskSVN extends MultiSVNBasedScript {

	@Override
	public void exec() {
		List<String> lista = getFilesListForSelectedTasks();
		Collections.sort(lista);

		if (!lista.isEmpty()) {
			File dir = null;
			CustomFileChooser jfc2 = new CustomFileChooser();
			jfc2.setFileSelectionMode(CustomFileChooser.DIRECTORIES_ONLY);
			int result = jfc2.showSaveDialog(null);
			if (result == CustomFileChooser.APPROVE_OPTION) {
				dir = jfc2.getSelectedFile();
			}

			String exportList = "";
			String relativePath;
			for (String file : lista) {
				
				relativePath = LocalUtil.getRelativePath(file);
				
				if(dir != null) {
					String str = SVNUtil.getFileFromSVN(file);
					try {
						File newFile = new File(dir, relativePath);
						if(!newFile.getParentFile().exists())
							newFile.getParentFile().mkdirs();
						FileUtil.writeFile(new File(dir, relativePath), str);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
								
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
		} else
			Display.write("No hay ficheros para las incidencias seleccionadas");
	}

	@Override
	public String getName() {
		return "Generar paquete a partir de un listado de incidencias";
	}
}
