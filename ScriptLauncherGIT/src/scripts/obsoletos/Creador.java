package scripts.obsoletos;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import scripts.common.Script;
import ui.CreadorGUI;

import launcher.OptionManager;

import common.ErrorManager;
import common.properties.DefaultProperties;
import common.properties.PropertiesManager;
import common.util.FileUtil;
import common.util.LocalUtil;

public class Creador extends Script {

	private CreadorGUI gui = null;

	public void exec() {
		gui = new CreadorGUI(this);
	}

	public boolean generaIncidencia(String paquete, String id, String nombre) {
		String nombreCompleto = ((id == null) || (id.equals(""))) ? nombre : id
				+ " - " + nombre;

		File dirPaquete = new File(DefaultProperties.getPackagesPath(), paquete);
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

		File dirPlantilla = new File(
				PropertiesManager.getLocalProperty("PathPlantilla"));
		if (!dirPlantilla.exists())
			return salir(ErrorManager.TEMPLATE_NOT_ACCESIBLE);
		if (!dirPlantilla.canRead())
			return salir(ErrorManager.TEMPLATE_NOT_READABLE);

		// Copiar plantilla al directorio de incidencias
		String newDirPath = dirPaquete.getAbsolutePath() + "\\"
				+ nombreCompleto;
		try {
			FileUtil.copyDirectory(dirPlantilla, new File(newDirPath));
		} catch (IOException e) {
			return salir(ErrorManager.NEW_DIR_CREATE_ERROR);
		}

		// Cambiar los nombres de los documentos
		updateDocumentName(new File(newDirPath), id, nombre, LocalUtil.getUserName());

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
				String newName = fileName.replace("--id--", id).replace(
						"--title--", nombre).replace("--author--", autor);
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
			JOptionPane.showMessageDialog(gui, ErrorManager.getErrorMessage(errno));
			return false;
		}
		
		return true;
	}

	@Override
	protected void releaseResources() {
		if (gui != null) {
			gui.setVisible(false);
			gui.removeAll();
			gui.dispose();
			gui = null;
		}
	}
	
	@Override
	public String getName() {
		return "Creador de incidencias";
	}

	@Override
	public boolean init(OptionManager om) {
		// TODO Auto-generated method stub
		return false;
	}
}


