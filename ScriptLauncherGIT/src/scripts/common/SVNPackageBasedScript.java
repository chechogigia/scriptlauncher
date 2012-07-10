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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListSelectionModel;

import com.taskadapter.redmineapi.bean.Project;

import launcher.OptionManager;


import ui.SeleccionGenerica;
import ui.SeleccionProyectoRedmine;

import common.util.RedmineUtil;
import common.util.SVNChange;
import common.util.SVNUtil;

public abstract class SVNPackageBasedScript extends SVNBasedScript {

	/**
	 * 
	 */
	private Project project = null;

	/**
	 * 
	 */
	private String versionName = "";

	/**
	 * 
	 */
	private List<String> availableStatus = null;
	
	/**
	 * 
	 * @param om
	 * @return Generated script
	 */
	@Override
	public boolean init(OptionManager om) {

		// Acceso a la informacion de tareas del redmine
		RedmineUtil.initRedmine();
		List<Project> projects = RedmineUtil.getProjects();

		// Seleccion de proyecto
		SeleccionProyectoRedmine ui = new SeleccionProyectoRedmine(projects);
		if (ui.isCanceled())
			return false;
		project = ui.getSelectedValue();
		ui.dispose();

		// Seleccion de version
		List<String> versions = RedmineUtil.getVersionsForProject(project);
		switch (versions.size()) {
		case 0:
			break;
		case 1:
			versionName = versions.get(0);
			break;
		default:
			SeleccionGenerica versionui = new SeleccionGenerica(versions, "Seleccion de version");
			if (!versionui.isCanceled())
				versionName = versionui.getSelectedValue();
			versionui.dispose();
		}

		// Seleccion de estados
		List<String> status = RedmineUtil.getStatusForProject(project);
		switch (status.size()) {
		case 0:
			break;
		default:
			SeleccionGenerica statusui = new SeleccionGenerica(status, "Seleccion de estados", ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			if (!statusui.isCanceled())
				availableStatus = statusui.getSelectedValues();
			statusui.dispose();
		}

		// Inicializacion del repositorio
		repository = SVNUtil.initRepository();
		if (repository == null)
			return false;

		return true;
	}

	/**
	 * 
	 * @return Map of files for selected project with a list (for each file) of
	 *         task that adds/modifies file
	 */
	protected final Map<String, List<Long>> getFileListForSelectedProject(List<String> validStatus) {
		return getFileListForProject(getProject(), validStatus);
	}

	/**
	 * 
	 * @param project
	 * @return List of files for project
	 */
	protected Map<String, List<Long>> getFileListForProject(Project project, List<String> validStatus) {
		Map<String, List<Long>> map = new HashMap<String, List<Long>>();

		List<String> taskList = RedmineUtil.getTaskList(getProject().getIdentifier(), getVersionName(), validStatus);

		for (String task : taskList) {
			Long value = RedmineUtil.getTaskId(task);
			if (value != null)
				for (SVNChange c : SVNUtil.getChangesForTask(value.toString())) {
					char type = c.getType();
					String path = c.getPath();
					if (type == 'A' || type == 'M') {
						List<Long> list = (map.keySet().contains(path)) ? map.get(path) : new ArrayList<Long>();
						if (!list.contains(value))
							list.add(value);
						map.put(path, list);
					} else if (type == 'D' && map.keySet().contains(path))
						map.remove(path);
				}
		}

		return map;
	}

	/**
	 * Returns the project
	 * 
	 * @return Project
	 */
	protected Project getProject() {
		return project;
	}

	/**
	 * Returns the version name
	 * 
	 * @return Version name
	 */
	protected String getVersionName() {
		return versionName;
	}

	/**
	 * 
	 * @return the Status
	 */
	protected List<String> getStatus() {
		return availableStatus;
	}
}
