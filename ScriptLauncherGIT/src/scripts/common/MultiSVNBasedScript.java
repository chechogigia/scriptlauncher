/* 
 * 1. Identificacion:
 *    Fichero        : MultiSVNBasedScript.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 03/10/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   03/10/2011 S.Ruiz   Primera implementacion.
 */
package scripts.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import launcher.OptionManager;

import ui.SeleccionIncidenciaRedmine;

import common.properties.RedmineProperties;
import common.util.RedmineUtil;
import common.util.SVNUtil;

public abstract class MultiSVNBasedScript extends SVNBasedScript {

	/**
	 * 
	 */
	protected List<String> taskIds = new ArrayList<String>();
	
	/**
	 * 
	 * @param om
	 * @return Generated script
	 */
	@Override
	public boolean init(OptionManager om) {

		// Acceso a la informacion de tareas del redmine
		RedmineUtil.initRedmine();
		Map<String,List<String>> taskMap = RedmineUtil.getTaskMap(RedmineProperties.getProjectId(), RedmineProperties.getCurrentVersion());
		
		// Seleccion de tarea
		SeleccionIncidenciaRedmine ui = new SeleccionIncidenciaRedmine(taskMap, true);
		if (ui.isCanceled()) {
			ui.dispose();
			return false;
		}
		String[] selection = ui.getSelectedValues();
		ui.dispose();

		for (String item : selection) {
			Long value = RedmineUtil.getTaskId(item);
			if (value != null)
				taskIds.add(value.toString());
		}

		// Inicializacion del repositorio
		repository = SVNUtil.initRepository();
		if(repository == null)
			return false;
		
		return true;
	}
	
	/**
	 * 
	 * @return List of files for selected task
	 */
	protected final List<String> getFilesListForSelectedTasks() {
		List<String> files = new ArrayList<String>();
		for (String taskId : taskIds)
			files.addAll(getFileListForTaskId(taskId));
		return files;
	}
	
	/**
	 * 
	 * @return List os selected task
	 */
	protected final List<String> getSelectedTasks() {
		return taskIds;
	}
}
