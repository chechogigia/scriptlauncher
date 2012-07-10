/*****************************************************************************
 * Name:   ErrorManager.java                                                 *
 * Author: Sergio Ruiz                                                       *
 * Date:   -                                                    		     *
 * --------------------------------------------------------------------------*
 * Descr.: Simple file structure operations, with no exception management.   *
 * --------------------------------------------------------------------------*
 * History:                                                                  *
 * 	1.0		-			S. Ruiz		Initial version	        		      	 *
 * 	1.1		15/10/10	S. Ruiz		Moved to common package					 *
 * 									Use of constants to identify errors		 *
 * 									New method getErrorMessage				 *			
 *****************************************************************************/
package common;

import strings.Messages;

/**
 * 
 * @author S. Ruiz
 * 
 */
public class ErrorManager {

	/**
	 * All Ok - Work done!
	 */
	public final static int ALL_OK = 0;

	/* Problems for launcher */
	public static String getLauncherErrorMessage(int errno) {
		return Messages.getString("Launcher.Error."+errno);
	}
	
	/* Problems for creator script */
	public final static int NO_PACKAGE = 1;
	public final static int PACKAGE_DIR_READ_ERROR = 2;
	public final static int PACKAGE_DIR_WRITE_ERROR = 3;
	public final static int PACKAGE_NOT_DIRECTORY = 4;
	public final static int CR_ALREADY_EXISTS = 5;
	public final static int TEMPLATE_NOT_ACCESIBLE = 6;
	public final static int TEMPLATE_NOT_READABLE = 7;
	public final static int NEW_DIR_CREATE_ERROR = 8;
	public final static int DESC_FILE_EDIT_ERROR = 9;

	/* Problems for sicnronizator script */

	/* Problems for remote launcher script */

	/* Other problems */

	/**
	 * Returns the associated error message for a specific number
	 * 
	 * @param errno
	 * @return Error message
	 */
	public static String getErrorMessage(int errno) {
		return Messages.getString("ErrorManager.10") + Messages.getString("ErrorManager." + errno); //$NON-NLS-1$
	}
}
