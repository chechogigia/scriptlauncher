/* 
 * 1. Identificacion:
 *    Fichero        : Change.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 12/09/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   12/09/2011 S.Ruiz   Primera implementacion.
 */
package common.util;

public class SVNChange {
	char type = 'A';
	String path = "";
	
	public char getType() {
		return type;
	}
	public String getPath() {
		return path;
	}
}