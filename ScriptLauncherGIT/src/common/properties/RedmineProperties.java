/* 
 * 1. Identificacion:
 *    Fichero        : RedmineProperties.java
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

public class RedmineProperties {
		
	private static final String ServerURLKey = "RedmineURL";
	
	private static final String QueryAPIKey = "RedmineQueryAPI";
	
	private static final String CurentVersionKey = "CurrentVersion";
	
	private static final String ProjectIDKey = "ProjectId";
	
	private static final String CacheFilePathKey = "CacheFilePath";
	
	private static final String QueryIdKey = "QueryId";
	
	public static String getServerURL() {
		return PropertiesManager.getProperty(DefaultProperties.Redmine, ServerURLKey);
	}
	
	public static String getQueryAPI() {
		return PropertiesManager.getProperty(DefaultProperties.Redmine, QueryAPIKey);
	}
	
	public static String getCurrentVersion() {
		return PropertiesManager.getProperty(DefaultProperties.Redmine, CurentVersionKey);
	}
	
	public static String getProjectId() {
		return PropertiesManager.getProperty(DefaultProperties.Redmine, ProjectIDKey);
	}
	
	public static String getCacheFilePath() {
		return PropertiesManager.getProperty(DefaultProperties.Redmine, CacheFilePathKey);
	}
	
	public static Integer getQueryId () {
		String queryIdStr = PropertiesManager.getProperty(DefaultProperties.Redmine, QueryIdKey);
		try {
			return Integer.valueOf(queryIdStr);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
}
