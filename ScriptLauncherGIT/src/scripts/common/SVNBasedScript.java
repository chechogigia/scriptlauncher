package scripts.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import launcher.OptionManager;

import org.tmatesoft.svn.core.io.SVNRepository;

import common.properties.RedmineProperties;
import common.util.RedmineUtil;
import common.util.SVNChange;
import common.util.SVNUtil;

import ui.Display;
import ui.SeleccionIncidenciaRedmine;

/**
 * 
 * @author sruizabad
 * 
 */
public abstract class SVNBasedScript extends Script {

	/**
	 * 
	 */
	private String taskId = null;

	/**
	 * 
	 */
	protected SVNRepository repository = null;
	
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
		SeleccionIncidenciaRedmine ui = new SeleccionIncidenciaRedmine(taskMap,false);
		if (ui.isCanceled()) {
			ui.removeAll();
			ui.dispose();
			ui = null;
			return false;
		}
		String selection = ui.getSelectedValue();
		ui.removeAll();
		ui.dispose();
		ui = null;

		Long value = RedmineUtil.getTaskId(selection);
		if (value != null)
			taskId = value.toString();

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
	protected final List<String> getFileListForSelectedTask() {
		return getFileListForTaskId(getTaskId());
	}
	
	/**
	 * 
	 * @param taskId
	 * @return List of files for task id
	 */
	protected List<String> getFileListForTaskId(String taskId) {
		List<String> lista = new ArrayList<String>();
		
		for (SVNChange c : SVNUtil.getChangesForTask(taskId)) {
			Display.debug("getFileListForTaskId - change (%s) %s", c.getType(), c.getPath());
			char type = c.getType();
			String path = c.getPath();
			if ((type == 'A' || type == 'M') && !lista.contains(path))
				lista.add(path);
			else if (type == 'D' && lista.contains(path))
				lista.remove(path);
		}

		return lista;
	}

	protected void releaseResources() {
		if (repository != null)
			repository.closeSession();
		
		RedmineUtil.closeRedmine();
		
		System.gc();
	}

	/**
	 * Returns the task id
	 * 
	 * @return task id
	 */
	protected String getTaskId() {
		Display.debug("getTaskId() devuelve %s", taskId);
		return taskId;
	}

	/**
	 * Returns the SVN Repository class
	 * 
	 * @return Repository
	 */
	protected SVNRepository getRepository() {
		return repository;
	}
}
