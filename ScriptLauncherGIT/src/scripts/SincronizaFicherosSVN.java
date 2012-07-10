/*****************************************************************************
 * Name:   SincronizaFicheros.java                                           *
 * Author: Sergio Ruiz / Rodrigo Lanza                                       *
 * Date:   -                                                                 *
 * --------------------------------------------------------------------------*
 * Descr.: This class is used to get whatever property you need from file    *
 *         Properties.txt                                                    *
 * --------------------------------------------------------------------------*
 * History:                                                                  *
 * 	1.0		-			R. Lanza		Initial version  			         *
 * 	1.1		-			S. Ruiz			Code adapted to application          *
 * 	1.2		14/10/10	S. Ruiz			Constants changed, use of properties *
 * 										and hardcoded lines removed          *		
 *****************************************************************************/
package scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import scripts.common.SVNBasedScript;
import ui.Display;
import ui.LauncherConf;

import common.CommandExecuter;
import common.Connection;
import common.Constants;
import common.properties.PropertiesManager;
import common.util.LocalUtil;
import common.util.SFTPUtil;
import common.util.SessionUtil;

public class SincronizaFicherosSVN extends SVNBasedScript {

	private String backupString;

	private Long ultimaSincronizacion = null;

	private boolean procesadoFicherosJavaCustomUpstream = false;
	private boolean procesadoFicherosJavaCoreUpstream = false;

	private boolean procesadoFicherosJavaCustomBuyer = false;
	private boolean procesadoFicherosJavaCoreBuyer = false;

	private boolean checkmetaUpstream = false;
	private boolean checkmetaBuyer = false;

	public void exec() {

		String taskId = getTaskId();
		if (taskId == null)
			return;

		String selectedFilesPath = LocalUtil.getCurrentPackageDirectory().concat("Task").concat(getTaskId()).concat(Constants.SELECTED_FILES_NAME);
		List<String> previousSelectedFiles = LocalUtil.readLines(selectedFilesPath);

		List<String> lista = getFileListForSelectedTask();
		List<String> fileList = new ArrayList<String>();
		for (String file : lista) {
			fileList.add(LocalUtil.getRelativePath(file));
		}

		LauncherConf lc = new LauncherConf(fileList, previousSelectedFiles);
		boolean canceled = lc.isCanceled();
		boolean comp = lc.getComp();
		boolean check = lc.getCheck();
		List<String> items = lc.getItems();
		lc.dispose();
		lc = null;

		if (canceled) {
			return;
		} else {
			inicializacion(conn);
			sincroniza(conn, comp, items);

			if (checkmetaUpstream && check) {
				Display.log("Ejecutando checkmeta sobre upstream ...");
				SessionUtil.execRemoteCommand(conn.getSession(), PropertiesManager.getProperty(conn.getEnvironment(), "upstreamcommand"));
				Display.log("Checkmeta terminado");
			}

			if (checkmetaBuyer && check) {
				Display.log("Ejecutando checkmeta sobre buyer ...");
				SessionUtil.execRemoteCommand(conn.getSession(), PropertiesManager.getProperty(conn.getEnvironment(), "buyercommand"));
				Display.log("Checkmeta terminado");
			}

			LocalUtil.writeLines(selectedFilesPath, items);
		}
	}

	public void sincroniza(Connection conn, boolean compilarSiNecesario, List<String> files) {
		conn.initSftpChannel();

		String environment = conn.getEnvironment();

		sincronizaFicheros(conn, environment, files);

		try {
			String lastSyncFilePath = LocalUtil.getCurrentPackageDirectory().concat("Task").concat(getTaskId()).concat(Constants.ULTIMA_SINC_NAME);
			BufferedWriter out = new BufferedWriter(new FileWriter(lastSyncFilePath));
			out.write(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
			out.close();
		} catch (Exception e) {
		}

		if (procesadoFicherosJavaCustomUpstream && compilarSiNecesario) {
			Display.log("Se han modificado ficheros java customizados-> compilado automatico ...");
			SessionUtil.execRemoteCommand(conn.getSession(), PropertiesManager.getProperty(environment, "compileUpstreamCommand"));
			Display.log("Fin del compilado automatico");
		}

		if (procesadoFicherosJavaCoreUpstream && compilarSiNecesario) {
			Display.log("Se han modificado ficheros java core-> compilado automatico ...");
			SessionUtil.execRemoteCommand(conn.getSession(), PropertiesManager.getProperty(environment, "compileUpstreamCoreCommand"));
			Display.log("Fin del compilado automatico");
		}

		if (procesadoFicherosJavaCustomBuyer && compilarSiNecesario) {
			Display.log("Se han modificado ficheros java customizados-> compilado automatico ...");
			SessionUtil.execRemoteCommand(conn.getSession(), PropertiesManager.getProperty(environment, "compileBuyerCustomCommand"));
			Display.log("Fin del compilado automatico");
		}

		if (procesadoFicherosJavaCoreBuyer && compilarSiNecesario) {
			Display.log("Se han modificado ficheros java core-> compilado automatico ...");
			// SessionUtil.execRemoteCommandShowErr(session,
			// PropertiesManager.getProperty(environment,"compileUpstreamCommand"));
			Display.log("Sin implementar");
			Display.log("Fin del compilado automatico");
		}

		conn.closeSftpChannel();
	}

	private void inicializacion(Connection conn) {
		String username = PropertiesManager.getProperty("Local", "UserName");
		if (username == null || username.length() == 0)
			backupString = ".".concat(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())).concat(".");
		else
			backupString = ".".concat(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())).concat(".").concat(username);

		try {
			String lastSyncFilePath = LocalUtil.getCurrentPackageDirectory().concat("Task").concat(getTaskId()).concat(Constants.ULTIMA_SINC_NAME);
			BufferedReader in = new BufferedReader(new FileReader(lastSyncFilePath));

			while (in.ready()) {
				String line = in.readLine();
				if (line != null) {
					ultimaSincronizacion = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").parse(line).getTime();
					break;
				}
			}

			in.close();
		} catch (Exception e) {
		}
	}

	private boolean isUpstream(String path) {
		return path.contains("Upstream");
	}

	private String getRelativePathFromServer(String path, boolean isUpstream) {
		int index = path.indexOf("\\");
		if (index == -1) {
			index = path.indexOf("/");
		}
		// Display.log("Index: " + index);
		String newPath = path.substring(index + 1);
		// Display.log("Path: " + path);
		return newPath;
	}

	private List<String> sincronizaFicheros(Connection conn, String environment, List<String> lista) {
		List<String> sincronizados = new ArrayList<String>();

		String tempPath = PropertiesManager.getProperty("Local", "TempPath");
		String compareCommand = PropertiesManager.getProperty("Local", "CompareCommand");
		String title1Command = " /title1:\"Local(";
		String title2Command = " /title2:\"Remoto(";

		// Por cada fichero de la lista
		Iterator<String> it = lista.iterator();
		while (it.hasNext()) {
			String originalPath = it.next();
			boolean isUpstream = isUpstream(originalPath);
			String path = getRelativePathFromServer(originalPath, isUpstream);
			String localPath = getLocalPath(path, isUpstream);
			String remotePath = getRemotePath(environment, path, isUpstream);

			Display.log("Procesando el fichero " + path);

			File file = new File(localPath);

			if ((ultimaSincronizacion != null) && (ultimaSincronizacion >= file.lastModified())) {
				Display.log("No cambio desde la ultima sincronizacion. Se salta este fichero.");
				continue;
			}

			// Fichero baja fichero remoto
			String remoteFileOnLocal = tempPath + file.getName() + "_tmp";

			if (SFTPUtil.getRemoteFile(conn.getSftpChannel(), remotePath, remoteFileOnLocal)) {

				Display.log("El fichero existe en el sistema remoto");

				String remoteFileCopyPath = "";

				// El fichero existe en el sistema remoto y se ha descargado en
				// remoteFileOnLocal
				if (!LocalUtil.areIdentical(file.getAbsolutePath(), remoteFileOnLocal)) {

					Display.log("Existen diferencias entre ambos ficheros. Se procede a hacer el merge");

					try {
						// Crea copia del fichero para poder comprobar luego si
						// se modificó el fichero durante el merge
						remoteFileCopyPath = remoteFileOnLocal + "bak";
						LocalUtil.copyfile(remoteFileOnLocal, remoteFileCopyPath);
						// Merge
						String fullCommand = compareCommand + title1Command + file.getAbsolutePath() + ")\"" + title2Command + remoteFileOnLocal + ")\" "
								+ "\"" + file.getAbsolutePath() + "\" \"" + remoteFileOnLocal + "\"";
						// String fullCommand = compareCommand + " \"" +
						// file.getAbsolutePath() + "\" \"" + remoteFileOnLocal
						// + "\"";
						Process p = Runtime.getRuntime().exec(fullCommand);
						p.waitFor();
						// Comprueba si se produjo algún cambio durante el merge
						if (!LocalUtil.areIdentical(remoteFileOnLocal, remoteFileCopyPath)) {
							Display.log("Se crea un backup en el sistema remoto (" + remotePath + backupString + ")");
							SFTPUtil.renameRemoteFile(conn.getSftpChannel(), remotePath, remotePath + backupString);
							Display.log("Se sube la versión local del fichero remoto mergeada");
							SFTPUtil.putLocalFile(conn.getSftpChannel(), remoteFileOnLocal, remotePath);
							actualizaControles(file, isUpstream);
							sincronizados.add(originalPath);

						} else {
							Display.log("El fichero remoto no se modifica.");
						}
					} catch (Exception e) {
						Display.log("Se produjo un error inesperado: " + e.getMessage());
						e.printStackTrace();
					}
				} else {
					Display.log("No existen diferencias entre ambos ficheros. No se hace nada");
				}

				LocalUtil.deleteLocalFile(remoteFileOnLocal);
				LocalUtil.deleteLocalFile(remoteFileCopyPath);

			} else {
				LocalUtil.deleteLocalFile(remoteFileOnLocal);
				Display.log("Se sube la versión local del fichero");
				// El fichero remoto no existe, se sube el local
				// Crea los directorios por si no existen mediante la ejecución
				// de un mkdir -p remoto
				new CommandExecuter(conn, "mkdir -p " + getRemoteParentDir(remotePath));
				// Sube el fichero
				SFTPUtil.putLocalFile(conn.getSftpChannel(), file.getAbsolutePath(), remotePath);
				actualizaControles(file, isUpstream);
				sincronizados.add(originalPath);
			}
		}

		return sincronizados;
	}

	public String getRemoteParentDir(String filePath) {
		int pos = filePath.lastIndexOf("/");

		if (pos < 0) {
			return null;
		}

		return filePath.substring(0, pos);
	}

	private String getLocalPath(String path, boolean isUpstream) {
		String localPath = null;

		if (isUpstream)
			localPath = PropertiesManager.getProperty("Local", "UpstreamPath");
		else
			localPath = PropertiesManager.getProperty("Local", "BuyerPath");

		if (!localPath.endsWith("\\") && !localPath.endsWith("/"))
			localPath = localPath.concat("/");

		return localPath.concat(path).replace('\\', '/');
	}

	private String getRemotePath(String environment, String path, boolean isUpstream) {
		String remotePath = null;

		if (isUpstream)
			remotePath = PropertiesManager.getProperty(environment, "remotePathUpstream");
		else
			remotePath = PropertiesManager.getProperty(environment, "remotePathBuyer");

		if (!remotePath.endsWith("\\") && !remotePath.endsWith("/"))
			remotePath = remotePath.concat("/");

		return remotePath.concat(path).replace('\\', '/');
	}

	private void actualizaControles(File file, boolean isUpstream) {
		boolean core = file.getPath().contains("src.core");

		// Compilacion automatica
		if (file.getName().endsWith(".java"))
			if (isUpstream) {
				if (core)
					procesadoFicherosJavaCoreUpstream = true;
				else
					procesadoFicherosJavaCustomUpstream = true;
			} else {
				if (core)
					procesadoFicherosJavaCoreBuyer = true;
				else
					procesadoFicherosJavaCustomBuyer = true;
			}

		// Checkmeta
		if (isUpstream)
			checkmetaUpstream = true;
		else
			checkmetaBuyer = true;
	}

	@Override
	public String getName() {
		return "Sincroniza incidencia con entorno de TCE";
	}

	@Override
	public boolean needsConnection() {
		return true;
	}
}
