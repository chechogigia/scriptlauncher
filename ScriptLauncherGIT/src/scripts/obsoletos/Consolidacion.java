package scripts.obsoletos;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;

import scripts.common.Script;
import ui.Display;

import launcher.OptionManager;

import common.properties.DefaultProperties;
import common.properties.PropertiesManager;
import common.util.LocalUtil;
import common.util.SFTPUtil;
import common.util.filelistreader.FileListReader;

public class Consolidacion extends Script {

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

		// Obtenemos el listado de ficheros del paquete
		List<String> listadoCompleto = new ArrayList<String>();
		File[] files = dp.listFiles();
		for (int i = 0, s = files.length; i < s; i++) {
			if (!files[i].isDirectory() || !files[i].canWrite()) {
				break;
			}

			String fullProjectFilePath = getFullProjectFileName(files[i]);
			if (fullProjectFilePath != null) {

				FileListReader reader = LocalUtil.getDefaultReader();
				List<String> fileList = reader.readFileList(fullProjectFilePath);
				if (fileList != null) {
					for (String path : fileList) {
						// LocalUtil.copyfile(LocalUtil.getLocalPath(path.substring(path.indexOf("\\") + 1), path.startsWith("Upstream")),
						// outputDir.getAbsolutePath() + "/" + path);
						if (!listadoCompleto.contains(path))
							listadoCompleto.add(path);
					}
				}
			}
		}

		// Conexion y descarga de los ficheros del entorno elegido
		conn.initSftpChannel();
		String environment = conn.getEnvironment();

		Iterator<String> it = listadoCompleto.iterator();
		while (it.hasNext()) {
			String originalPath = it.next();
			boolean isUpstream = isUpstream(originalPath);
			String path = getRelativePathFromServer(originalPath);
			String localPath = getLocalPath(outputDir.getAbsolutePath(), path, isUpstream);
			String remotePath = getRemotePath(environment, path, isUpstream);

			File file = new File(localPath);
			File parent = new File(file.getParent());

			Display.log("Procesando el fichero " + path);
			Display.log(" - Local path: " + localPath);
			Display.log(" - Remote path: " + remotePath);
			Display.log(" - Parent local path: " + parent);

			if (!(parent.exists())) {
				parent.mkdirs();
			}
			SFTPUtil.getRemoteFile(conn.getSftpChannel(), remotePath, localPath);

		}

		conn.closeSftpChannel();
	}

	private static String getFullProjectFileName(File directorioIncidendia) {
		String prjFileName = LocalUtil.getProjectFileName(directorioIncidendia);
		if (prjFileName == null) {
			System.out.println("Error: Fichero de proyecto no encontrado para la incidencia directorioIncidencia: "
					+ directorioIncidendia.getName());
			return null;
		}

		return directorioIncidendia.getAbsolutePath() + "\\" + prjFileName;
	}

	private boolean isUpstream(String path) {
		return path.startsWith("Upstream");
	}

	private String getRelativePathFromServer(String path) {
		int index = path.indexOf("\\");
		path = path.substring(index + 1);
		return path;
	}

	private String getLocalPath(String outputDir, String path, boolean isUpstream) {
		if (!outputDir.endsWith("\\") && !outputDir.endsWith("/"))
			outputDir = outputDir.concat("/");

		if (isUpstream)
			return outputDir.concat("Upstream/").concat(path).replace('\\', '/');
		return outputDir.concat("Buyer/").concat(path).replace('\\', '/');
	}

	private String getRemotePath(String environment, String path, boolean isUpstream) {
		String remotePath = (isUpstream) ? PropertiesManager.getProperty(environment, "remotePathUpstream") : PropertiesManager
				.getProperty(environment, "remotePathBuyer");

		if (!remotePath.endsWith("\\") && !remotePath.endsWith("/"))
			remotePath = remotePath.concat("/");

		return remotePath.concat(path).replace('\\', '/');
	}

	@Override
	public String getName() {
		return "Consolidacion";
	}

	@Override
	public boolean init(OptionManager om) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean needsConnection() {
		return true;
	}
}