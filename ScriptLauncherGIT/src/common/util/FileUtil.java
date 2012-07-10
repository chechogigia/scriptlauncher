/*****************************************************************************
 * Name:   FileUtil.java                                                     *
 * Author: Sergio Ruiz                                                       *
 * Date:   15/10/2010                                                        *
 * --------------------------------------------------------------------------*
 * Descr.: Simple file structure operations, with no exception management.   *
 * --------------------------------------------------------------------------*
 * History:                                                                  *
 * 	1.0		-			S. Ruiz		Initial version	        		      	 *
 * 	1.1		15/10/10	S. Ruiz		Moved to common package					 *			
 *****************************************************************************/
package common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author S. Ruiz
 *
 */
public class FileUtil {

	private static String EOL = "\n"; 
	
	/**
	 * Copy a file from source (srcDir) to destiny (dstDir)
	 * @param srcDir
	 * @param dstDir
	 * @throws IOException
	 */
	public static void copyDirectory(File srcDir, File dstDir) throws IOException {
		if (srcDir.isDirectory()) {
			if (!dstDir.exists()) {
				dstDir.mkdir();
			}

			String[] children = srcDir.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(srcDir, children[i]), new File(dstDir, children[i]));
			}
		} else {
			copyFile(srcDir, dstDir);
		}
	}

	/**
	 * Copy a file from source (src) to destiny (dst)
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Read the content of a file and put it in a String
	 * @param archivo
	 * @return File content
	 * @throws IOException
	 */
	public static String readFile(File archivo) throws IOException {
		String texto = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(archivo));
			String linea;
			while ((linea = br.readLine()) != null)
				texto += linea + EOL;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (null != br) {
					br.close();
				}
			} catch (IOException e2) {
				throw e2;
			}
		}

		return texto;
	}
	
	public static List<String> readLinesFromFile(File archivo) throws IOException
	{
		return Arrays.asList(readFile(archivo).split(EOL));
	}

	/**
	 * Write text into file.
	 * @param file
	 * @param text
	 * @throws IOException
	 */
	public static void writeFile(File file, String text) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(text);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (null != bw)
					bw.close();
			} catch (IOException e2) {
				throw e2;
			}
		}
	}
	
	/**
	 * Write lines into file.
	 * @param file
	 * @param lines
	 * @throws IOException
	 */
	public static void writeLinesToFile(File file, List<String> lines) throws IOException {
		
		String text = "";
		
		for (String line : lines) {
			text = text + line + EOL;
		}
		
		writeFile(file, text);
	}	
	
	/**
	 * Return a list with names of files from directory dir
	 * @param dir
	 * @param filter
	 * @return the List
	 */
	public static List<String> getFileNamesForDirectory(File dir, FilenameFilter filter) {
		if (dir == null || !dir.canRead() || !dir.isDirectory())
			return null;
		
		String [] filenames = dir.list(filter);
		List<String> returnedList = new ArrayList<String>();
		for(int i=0,s=filenames.length;i<s;i++) {
			int index = filenames[i].lastIndexOf(".");
			if(index > 0) {
				returnedList.add(filenames[i].substring(0, index));
			}
		}
			
		return returnedList;
	}
	
	/**
	 * Return a list of files from directory dir
	 * @param dir
	 * @param filter
	 * @return the List
	 */
	public static List<File> getFilesForDirectory(File dir, FilenameFilter filter) {
		if (dir == null || !dir.canRead() || !dir.isDirectory())
			return null;
		
		File [] files = dir.listFiles(filter);
		return Arrays.asList(files);
	}
}
