/* 
 * 1. Identificacion:
 *    Fichero        : ErrorMessage.java
 *    Autor          : S.Ruiz
 *    Version        : 1.0
 *    Fecha          : 15/06/2012
 *
 * 2. Proposito:
 *    New class for accessing to error messages in diferent locales
 *
 * 3. Historia de Revisiones:
 *    Ver   Fecha       Autor    	Razon
 *    ----- ----------- ------------ ------------------------------------------
 *    1.0   15/06/2012 S.Ruiz   Primera implementacion.
 */
package strings;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import common.properties.DefaultProperties;

public class ErrorMessage {
	private static final String BUNDLE_NAME = "strings.errormessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, DefaultProperties.getLocale());

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
