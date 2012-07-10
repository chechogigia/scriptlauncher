package common.util.filelistreader;

import java.util.List;

public interface FileListReader {
	/**
	 * Returns all files encountered in file
	 * @param filePath
	 * @return Files
	 */
	public List<String> readFileList(String filePath);
	
	/**
	 * Returns all files encountered in file that meets with the rfc name passed as argument
	 * @param filePath
	 * @param rfcName
	 * @return Files
	 */
	public List<String> readFileList(String filePath, String rfcName);
	
	/**
	 * Return all rfc names encountered in file
	 * @param filePath
	 * @return Files
	 */
	public List<String> readRFCList(String filePath);
}
