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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import common.util.FileUtil;
import common.util.LocalUtil;
import common.util.RedmineUtil;
import common.util.SVNUtil;

import scripts.common.SVNPackageBasedScript;
import ui.CustomFileChooser;
import ui.Display;


public class PaqueteSVN extends SVNPackageBasedScript {

	private static final String[] VALID_STATUS = new String[] { "En desarrollo", "Pendiente pruebas integración",
			"Pendiente pruebas de usuario", "Pendiente despliegue pro", "Pendiente de funcional" };

	@Override
	public void exec() {
		
		//Map<String, List<Long>> map = getFileListForSelectedProject(Arrays.asList(VALID_STATUS));
		List<String> selectedStatus = getStatus();
		if(selectedStatus == null || selectedStatus.isEmpty())
			selectedStatus = Arrays.asList(VALID_STATUS);
		Map<String, List<Long>> map = getFileListForSelectedProject(selectedStatus);
		List<String> lista = new ArrayList<String>(map.keySet());
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
				/************************************************/
				// For Debug
				List<Long> taskList = map.get(file);
				for (Long taskid : taskList)
					Display.debug(" ... from " + RedmineUtil.getTaskInfo(taskid.longValue()));
				/************************************************/
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
			Display.write("No hay ficheros para este proyecto");
	}

	@Override
	public String getName() {
		return "Generar paquete con los ficheros del SVN";
	}
}
