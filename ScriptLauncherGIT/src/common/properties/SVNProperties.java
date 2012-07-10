/* 
 * 1. Identificacion:
 *    Fichero        : SVNProperties.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 07/09/2011
 *
 * 2. Proposito:
 *    <Escribir aqui una breve descripcion de lo que debe hacer esta clase>
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   07/09/2011 S.Ruiz   Primera implementacion.
 */
package common.properties;

public class SVNProperties {
	
	private static final String RepositoryURLKey = "RepositoryURL";
	private static final String UserNameKey = "UserName";
	private static final String PasswordKey = "Password";
	private static final String CacheFilePathKey = "CacheFilePath";
	
	public static String getRepositoryURL() {
		return PropertiesManager.getProperty(DefaultProperties.SVN, RepositoryURLKey);
	}
	
	public static String getUserName(){
		return PropertiesManager.getProperty(DefaultProperties.SVN, UserNameKey);
	}
	
	public static String getUserPassword() {
		return PropertiesManager.getProperty(DefaultProperties.SVN, PasswordKey);
	}
	
	public static String getCacheFilePath() {
		return PropertiesManager.getProperty(DefaultProperties.SVN, CacheFilePathKey);
	}
}
