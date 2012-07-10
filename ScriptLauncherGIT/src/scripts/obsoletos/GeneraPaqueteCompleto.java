package scripts.obsoletos;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import scripts.common.Script;
import ui.Display;
import ui.GeneraPaqueteCompletoGUI;

import launcher.OptionManager;

import common.properties.DefaultProperties;
import common.util.LocalUtil;
import common.util.filelistreader.FileListReader;

public class GeneraPaqueteCompleto extends Script {

	public void exec() {
		File dp = null;
		JFileChooser packageChooser = new JFileChooser(DefaultProperties.getPackagesPath());
		packageChooser.setDialogTitle("Selección de paquete");
		packageChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = packageChooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		dp = packageChooser.getSelectedFile();

		if (!dp.exists() || !dp.isDirectory()) {
			Display.write("No se puede usar el directorio raiz '%s'", dp.getPath());
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Directorio de salida");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		returnVal = chooser.showSaveDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File outputDir = chooser.getSelectedFile();

		GeneraPaqueteCompletoGUI gui = new GeneraPaqueteCompletoGUI(dp);
		if (gui.isCanceled())
			return;
		String[] rfcs = gui.getSelectedFiles();
		gui.dispose();
		
		for (int i = 0, s = rfcs.length; i < s; i++) {

			FileListReader reader = LocalUtil.getDefaultReader();
			List<String> fileList = reader.readFileList(dp.getPath(), rfcs[i]);
			if (fileList != null) {
				for (String path : fileList) {
					LocalUtil.copyfile(LocalUtil.getLocalPath(path.substring(path.indexOf("/") + 1), path.startsWith("Upstream")), outputDir.getAbsolutePath()
							+ "/" + path);
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Generacion de paquete";
	}

	@Override
	public boolean init(OptionManager om) {
		// TODO Auto-generated method stub
		return false;
	}
}