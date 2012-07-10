/* 
 * 1. Identificacion:
 *    Fichero        : ListadoSVN.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 12/08/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
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

import scripts.common.MultiSVNBasedScript;
import ui.CustomFileChooser;
import ui.Display;


public class ListadoMultiTaskSVN extends MultiSVNBasedScript {

	@Override
	public void exec() {
		List<String> lista = getFilesListForSelectedTasks();
		Collections.sort(lista);

		String exportList = "";
		String relativePath;
		for (String file : lista) {
			relativePath = LocalUtil.getRelativePath(file);
			Display.write(relativePath);
			exportList += relativePath + "\n";
		}

		CustomFileChooser jfc = new CustomFileChooser(LocalUtil.getCurrentPackageDirectory(),"Seleccion del fichero de salida");
		if (jfc.showSaveDialog(null) == CustomFileChooser.APPROVE_OPTION) {
			try {
				FileUtil.writeFile(jfc.getSelectedFile(), exportList);
			} catch(IOException e) {
				Display.debug(e.getMessage());
			}
		}
	}

	@Override
	public String getName() {
		return "Listado ficheros de varias incidencias";
	}
}
