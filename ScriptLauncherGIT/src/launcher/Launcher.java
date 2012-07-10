package launcher;

import javax.swing.JOptionPane;

import scripts.common.Script;
import strings.ErrorMessage;
import ui.Display;

import common.ErrorManager;
import common.properties.PropertiesManager;
import common.util.ScriptWrapper;

/**
 * This class purpose is only to be the point of entrance to the application. In addition, this class class
 * 
 * @author S.Ruiz
 * 
 */
public class Launcher {

	/**
	 * ErrorKey: Launcher.ScriptError -> generic script error
	 */
	private static final String SCRIPTERROR = "Launcher.ScriptError";

	/**
	 * Main method. This method instantiates the Launcher class.
	 * 
	 * @param args
	 */
	public static void main(final String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Launcher(new OptionManager(args));
			}
		});
	}

	/**
	 * Launcher constructor. Checks args using an OptionManager object. Then, if no problems are encountered, it creates the script and
	 * executes it.
	 * 
	 * @param om
	 */
	public Launcher(OptionManager om) {

		// Step 1: check if the arguments are right
		int error = om.checkArgs();
		if (error != OptionManager.OK) {
			JOptionPane.showMessageDialog(null, ErrorManager.getLauncherErrorMessage(error));
			return;
		}

		// Step 2: set the properties file path
		PropertiesManager.setPropertiesFilePath(om.getPropertiesFile());

		// Step 3: choose the script
		if (om.getScriptWrapper() == null)
			om.setScriptWrapper(ScriptWrapper.selectScript());
		
		if (om.getScriptWrapper() == null) {
			Display.write("Sin script");
			return;
		}

		// Step 4: create, execute and terminate the script
		try {
			Script script = Script.create(om);
			script.execute();
			script.terminate();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, String.format(ErrorMessage.getString(SCRIPTERROR), e.getMessage())); //$NON-NLS-1$
			e.printStackTrace();
		}
	}
}