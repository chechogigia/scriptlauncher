package scripts.obsoletos;

import java.io.File;
import java.util.Collections;
import java.util.List;

import scripts.common.RFCBasedScript;
import ui.Display;


import common.properties.DefaultProperties;
import common.util.FileUtil;
import common.util.LocalUtil;
import common.util.filelistreader.FileListReader;

/**
 * 
 * @author sruizabad
 * 
 */
public class ListadoFicheros extends RFCBasedScript {
			
	/**
	 * 
	 */
	@Override
	public void exec() {
		FileListReader reader = LocalUtil.getDefaultReader();
		List<String> lista = reader.readFileList(getPackageDir(), getRFCName());
		Collections.sort(lista);
		if (lista != null) {
			try {
				String text = "";
				for (String line : lista) {
					text += line + "\n";
					Display.write(line);
				}
				FileUtil.writeFile(getOutputFile(), text);

			} catch (Exception e) {
				Display.write("Excepción: " + e.getMessage());
				e.printStackTrace(System.out);
			}
		}
	}

	@Override
	public String getName() {
		return "Listado de ficheros por incidencia";
	}

	private File getOutputFile() {
		return new File(LocalUtil.getDocumentDirectoryPath(getPackageDir(), getRFCName()), DefaultProperties.getListOutputFileName());
	}
}
