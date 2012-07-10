/* 
 * 1. Identificacion:
 *    Fichero        : CreadorFromSVN.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 22/11/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   22/11/2011 S.Ruiz   Primera implementacion.
 */
package scripts;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import scripts.common.ExtendedMultiSVNBasedScript;
import ui.Display;

import common.ErrorManager;
import common.properties.DefaultProperties;
import common.properties.PropertiesManager;
import common.util.FileUtil;
import common.util.LocalUtil;
import common.util.RedmineUtil;

public class CreadorFromSVN extends ExtendedMultiSVNBasedScript {

	@Override
	public void exec() {
		List<String> lista = getSelectedTasks();
		boolean asRider = getUseAsRider();

		for (String file : lista) {
			long id = new Long(file).longValue();
			String idSalesforce = RedmineUtil.getSalesforceIDFromTaskId(id);
			String name = RedmineUtil.getNameFromTaskId(id);

			generaIncidencia(idSalesforce, name, asRider);
		}
	}

	@Override
	public String getName() {
		return "Crear incidencia en proyecto local";
	}

	public boolean generaIncidencia(String id, String nombre, boolean asRider) {
		String nombreCompleto = ((id == null) || (id.equals(""))) ? nombre : id + " - " + nombre;

		File dirPaquete = new File(DefaultProperties.getPackagesPath(), DefaultProperties.getCurrentPackage());
		if (!dirPaquete.exists())
			return salir(ErrorManager.NO_PACKAGE);
		if (!dirPaquete.canRead())
			return salir(ErrorManager.PACKAGE_DIR_READ_ERROR);
		if (!dirPaquete.canWrite())
			return salir(ErrorManager.PACKAGE_DIR_WRITE_ERROR);
		if (!dirPaquete.isDirectory())
			return salir(ErrorManager.PACKAGE_NOT_DIRECTORY);
		if (yaExiste(dirPaquete, nombreCompleto))
			return salir(ErrorManager.CR_ALREADY_EXISTS);

		if (!asRider) {
			File dirPlantilla = new File(PropertiesManager.getLocalProperty("PathPlantilla"));
			if (!dirPlantilla.exists())
				return salir(ErrorManager.TEMPLATE_NOT_ACCESIBLE);
			if (!dirPlantilla.canRead())
				return salir(ErrorManager.TEMPLATE_NOT_READABLE);

			// Copiar plantilla al directorio de incidencias
			String newDirPath = dirPaquete.getAbsolutePath() + "\\" + nombreCompleto;
			File newDir = new File(newDirPath);
			try {
				FileUtil.copyDirectory(dirPlantilla, newDir);
			} catch (IOException e) {
				return salir(ErrorManager.NEW_DIR_CREATE_ERROR);
			}

			// Cambiar los nombres de los documentos
			updateDocumentName(newDir, id, nombre, LocalUtil.getUserName());

			try {
				newDirPath = newDir.getCanonicalPath();
			} catch (IOException e) {
			}

			Display.write("Incidencia %s (%s) creada en %s", id, nombre, newDirPath);
		} else {
			File dirPlantilla = new File(PropertiesManager.getLocalProperty("PathPlantillaRider"));
			if (!dirPlantilla.exists())
				return salir(ErrorManager.TEMPLATE_NOT_ACCESIBLE);
			if (!dirPlantilla.canRead())
				return salir(ErrorManager.TEMPLATE_NOT_READABLE);

			// Copiar plantilla al directorio de incidencias
			String newDirPath = dirPaquete.getAbsolutePath() + "\\Rider\\" + nombreCompleto;
			File newDir = new File(newDirPath);
			try {
				FileUtil.copyDirectory(dirPlantilla, newDir);
			} catch (IOException e) {
				return salir(ErrorManager.NEW_DIR_CREATE_ERROR);
			}

			// Cambiar los nombres de los documentos
			updateDocumentName(newDir, id, nombre, LocalUtil.getUserName());

			try {
				newDirPath = newDir.getCanonicalPath();
			} catch (IOException e) {
			}

			Display.write("Incidencia %s (%s) creada en %s", id, nombre, newDirPath);
		}

		return salir(ErrorManager.ALL_OK);
	}

	private void updateDocumentName(File file, String id, String nombre, String autor) {
		if ((id == null) || (id.equals("")))
			id = "NoId";

		if (file.isDirectory()) {
			File[] childs = file.listFiles();
			for (File child : childs)
				updateDocumentName(child, id, nombre, autor);
		} else if (file.isFile()) {
			String fileName = file.getName();
			if (fileName.contains("--id--") || fileName.contains("--title--") || fileName.contains("--author--")) {
				String newName = fileName.replace("--id--", id).replace("--title--", nombre).replace("--author--", autor);
				file.renameTo(new File(file.getParent(), newName));
			}
		}
	}

	private boolean yaExiste(File dir, String titulo) {
		String[] files = dir.list();
		for (int i = 0, s = files.length; i < s; i++) {
			if (titulo.equals(files[i]))
				return true;
		}
		return false;
	}

	private boolean salir(int errno) {
		if (errno > 0) {
			JOptionPane.showMessageDialog(null, ErrorManager.getErrorMessage(errno));
			return false;
		}

		return true;
	}

}
