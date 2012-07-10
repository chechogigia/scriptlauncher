/* 
 * 1. Identificacion:
 *    Fichero        : ExtendedMultiSVNBasedScript.java
 *    Autor          : sruizabad
 *    Version        : 1.0
 *    Fecha          : 02/07/2012
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   02/07/2012 sruizabad   Primera implementacion.
 */
package scripts.common;

import java.util.List;
import java.util.Map;

import launcher.OptionManager;

import ui.SeleccionIncidenciaRedmine;

import common.properties.RedmineProperties;
import common.util.RedmineUtil;
import common.util.SVNUtil;

public abstract class ExtendedMultiSVNBasedScript extends MultiSVNBasedScript {

	protected boolean useAsRider = false;
	
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
		SeleccionIncidenciaRedmine ui = new SeleccionIncidenciaRedmine(taskMap, true,true);
		if (ui.isCanceled()) {
			ui.dispose();
			return false;
		}
		String[] selection = ui.getSelectedValues();
		useAsRider = ui.getExtendedValue(); 
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
	 * @return
	 */
	public boolean getUseAsRider(){
		return useAsRider;
	}

}
