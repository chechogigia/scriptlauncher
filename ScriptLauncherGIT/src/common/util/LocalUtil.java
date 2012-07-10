package common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import common.Constants;
import common.classes.ClassUtil;

import common.properties.DefaultProperties;
import common.properties.PropertiesManager;
import common.util.filelistreader.FileListReader;

public class LocalUtil {

	public static FileListReader getDefaultReader() {
		return (FileListReader)ClassUtil.instanceClass(DefaultProperties.getSourceReader());
	}

	public static void writeLines(String path, List<String> lines) {
		try {
			FileUtil.writeLinesToFile(new File(path), lines);
		} catch (Exception e) {
		}
	}

	public static List<String> readLines(String path) {
		try {
			return FileUtil.readLinesFromFile(new File(path));
		} catch (Exception e) {
		}

		return new ArrayList<String>();
	}	

	public static String copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f3 = new File(dtFile);
			File f2 = f3.getParentFile();
			if (!f2.exists())
				f2.mkdirs();
			f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return "File copied.";
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage() + " in the specified directory.");
			return ex.getMessage() + " in the specified directory.";
		} catch (IOException e) {
			return e.getMessage();
		}
	}

	public static boolean areIdentical(String file1, String file2) {
		// Primero comparamos tamaños (si es distintos asumimos diferencias)
		if (new File(file1).length() != new File(file2).length())
			return false;

		// Sino se comprueba token a token (puede llegar a ser muy lento en ficheros grandes)
		try {
			// Extraído de
			// http://blog.taragana.com/index.php/archive/java-program-compare-two-text-files/
			String s1 = "";
			String s2 = "", s3 = "", s4 = "";
			String y = "", z = "";

			// Reading the contents of the files
			BufferedReader br = new BufferedReader(new FileReader(file1));
			BufferedReader br1 = new BufferedReader(new FileReader(file2));

			while ((z = br1.readLine()) != null)
				s3 += z;

			while ((y = br.readLine()) != null)
				s1 += y;

			// String tokenizing
			//int numTokens = 0;
			StringTokenizer st = new StringTokenizer(s1);
			String[] a = new String[10000];
			for (int l = 0; l < 10000; l++) {
				a[l] = "";
			}
			int i = 0;
			while (st.hasMoreTokens()) {
				s2 = st.nextToken();
				a[i] = s2;
				i++;
				//numTokens++;
			}

			//int numTokens1 = 0;
			StringTokenizer st1 = new StringTokenizer(s3);
			String[] b = new String[10000];
			for (int k = 0; k < 10000; k++) {
				b[k] = "";
			}
			int j = 0;
			while (st1.hasMoreTokens()) {
				s4 = st1.nextToken();
				b[j] = s4;
				j++;
				//numTokens1++;
			}

			// comparing the contents of the files and printing the differences, if
			// any.
			for (int m = 0; m < a.length; m++) {
				if (!(a[m].equals(b[m]))) {
					return false;
				}
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void deleteLocalFile(String absolutePath) {
		File file = new File(absolutePath);
		file.delete();
	}

	public static String getRelativePath(String path) {
		int index = path.indexOf("Upstream");
		if (index > 0)
			return path.substring(index);

		index = path.indexOf("Buyer");
		if (index > 0)
			return path.substring(index);

		return path;
	}

	public static void deleteFileRecursive(File file) {
		if ((file == null) || (!file.exists()))
			return;

		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++)
				deleteFileRecursive(files[i]);
			file.delete();
		}
	}

	public static String getProjectFileName(File directory) {
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.getName().matches(Constants.PrjRegex)) {
				return file.getName();
			}
		}
		return null;
	}

	public static String getLocalPath(String path, boolean isUpstream) {
		String localPath = PropertiesManager.getProperty("Local", isUpstream?"UpstreamPath":"BuyerPath");
		return localPath + path;
	}
	
	public static String getDocumentDirectoryPath(String packageDir, String rfcName) {
		String docDirPath = (packageDir.endsWith("/"))?packageDir:packageDir.concat("/");
		return docDirPath.concat(rfcName).concat("/").concat(Constants.DOCDIR_NAME);
	}
	
	public static String getImplementationDirectoryPath(String packageDir, String rfcName) {
		String docDirPath = (packageDir.endsWith("/"))?packageDir:packageDir.concat("/");
		return docDirPath.concat(rfcName).concat("/").concat(Constants.IMPDIR_NAME);
	}
	
	public static String getCurrentPackageDirectory() {
		String pathPackages = DefaultProperties.getPackagesPath();
		String currentPackage = DefaultProperties.getCurrentPackage();
		return pathPackages.concat(currentPackage).concat("/");
	}
	
	public static boolean checkDirectoryFullAccess(String directoryPath) {
		File di = new File(directoryPath);
		return di.exists() && di.isDirectory() && di.canWrite();
	}
	
	public static String getUserName() {
		String userName = PropertiesManager.getLocalProperty(PropertiesManager.UserNameKey);
		if (userName == null)
			userName = "NoUser";
		return userName;
	}
}
