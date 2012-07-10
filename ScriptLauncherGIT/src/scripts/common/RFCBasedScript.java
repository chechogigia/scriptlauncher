package scripts.common;

import launcher.OptionManager;
import strings.Messages;
import ui.Display;
import ui.SeleccionIncidencia;

import common.util.LocalUtil;
import common.util.filelistreader.FileListReader;

/**
 * 
 * @author sruizabad
 * 
 */
public abstract class RFCBasedScript extends Script {

	/**
	 * 
	 */
	private static String rfcName = null;

	/**
	 * 
	 */
	private static String packageDir = null;

	/**
	 * 
	 * @param om
	 * @return Generated scripts
	 */
	@Override
	public boolean init(OptionManager om) {

		FileListReader reader = LocalUtil.getDefaultReader();
		packageDir = LocalUtil.getCurrentPackageDirectory();

		SeleccionIncidencia gui = new SeleccionIncidencia(reader.readRFCList(packageDir));
		if (gui.isCanceled())
			return false;
		rfcName = gui.getSelectedValue();
		gui.dispose();

		if (!LocalUtil.checkDirectoryFullAccess(getRFCDirectory())) {
			System.out.printf(Messages.getString("Launcher.4"), getRFCDirectory()); //$NON-NLS-1$
			return false;
		}

		Display.debug(String.format(Messages.getString("Launcher.9"), getRFCDirectory())); //$NON-NLS-1$

		return true;
	}

	/**
	 * Returns the request for change directory name selected
	 * 
	 * @return RFC Name
	 */
	protected static String getRFCName() {
		return rfcName;
	}

	/**
	 * 
	 * @return RFC directory
	 */
	protected static String getRFCDirectory() {
		return packageDir.concat(rfcName).replace("\\", "/") + "/";
	}

	/**
	 * Return the package directory
	 * 
	 * @return Package directory
	 */
	protected static String getPackageDir() {
		return packageDir;
	}
}
