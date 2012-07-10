/* 
 * 1. Identificacion:
 *    Fichero        : ClassFilenameFilter.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 20/12/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   20/12/2011 S.Ruiz   Primera implementacion.
 */
package common.classes;

import java.io.File;
import java.io.FilenameFilter;

public class ClassFilenameFilter implements FilenameFilter {

	private String className;
	
	public ClassFilenameFilter (String className) {
		this.className = className;
	}
	
	public ClassFilenameFilter () {}
	
	public boolean accept(File dir, String name) {
		if(className != null && (name.equals(className+".class")))
			return true;
		if(className == null && (name.endsWith(".class")))
			return true;
		return false;
	}

}