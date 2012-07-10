package common.util.filelistreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.Constants;

import ui.Display;
import ui.util.DirectoryWithProjectFileFilter;
import ui.util.ProjectFileFilter;

public class UltraEditProjectFileReader implements FileListReader {

	public List<String> readFileList(String filePath) {
		return readFileList(filePath,null);
	}
	
	public List<String> readFileList(String filePath, String rfcName) {
		try {
			File file = new File(filePath,rfcName);
			File [] files = file.listFiles(new ProjectFileFilter());
			if(files == null || files.length != 1)
				return null;
			
			FileReader fis = new FileReader(files[0]);
			BufferedReader in = new BufferedReader(fis);

			List<String> list = null;

			while (in.ready()) {
				String line = in.readLine();
				if (line.matches(Constants.DirRegex)) {
					list = readFileList(in);
				}
			}

			if (list != null && list.size() > 1)
				Collections.sort(list);

			return list;
		} catch (Exception e) {
			Display.log("Excepción: " + e.getMessage());
			e.printStackTrace(System.out);
			return new ArrayList<String>();
		}
	}

	private static List<String> readFileList(BufferedReader in) throws Exception {
		List<String> lista = new ArrayList<String>();
		while (in.ready()) {
			String line = in.readLine();
			if (line.matches(Constants.FileRegex)) {
				int splitIndex = line.lastIndexOf("=");
				String filePath = line.substring(splitIndex + 1);
				
				// Begin - Fix for recursive directory loading
				addPathToList(lista, filePath);
				// End - Fix for recursive directory loading
			}
		}
		return lista;
	}
	
	// Begin - Fix for recursive directory loading
	private static boolean isDir(String path)
	{
		File file = new File(path);
		return file.isDirectory();
	}
	
	private static void addPathToList(List<String> list, String path)
	{
		if(isDir(path))
		{
			addDirToList(list, path);
		}
		else
		{
			addFileToList(list, path);
		}
	}
	
	private static void addDirToList(List<String> list, String dirPath)
	{
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		for (File file : files)
		{
			addPathToList(list, file.getAbsolutePath());
		}
	}
	
	private static void addFileToList(List<String> list, String filePath)
	{
		int serverindex = filePath.lastIndexOf("Server");
		if (serverindex > 0) {
			String aplicacion = filePath.matches(".*Upstream.*") ? "Upstream" : "Buyer";
			String path = aplicacion + "\\" + filePath.substring(serverindex);
			list.add(path);
		}
	}
	// End - Fix for recursive directory loading

	public List<String> readRFCList(String filePath) {
		List<String> list = new ArrayList<String>();
		File file = new File(filePath);
		if(!(file.isDirectory() && file.canRead()))
			return list;
		
		DirectoryWithProjectFileFilter pff = new DirectoryWithProjectFileFilter();
		File [] rfcs = file.listFiles(pff);
		for(File f: rfcs) {
			list.add(f.getName());
		}
		
		return list;
	}
}
