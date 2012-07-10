package scripts.obsoletos;

import java.io.File;
import java.util.List;

import scripts.common.RFCBasedScript;


import common.util.LocalUtil;
import common.util.filelistreader.FileListReader;

public class GeneraPaquete extends RFCBasedScript {

	/**
	 * 
	 */
	@Override
	public void exec() {
		String impDirPath = LocalUtil.getImplementationDirectoryPath(getPackageDir(), getRFCName());
		LocalUtil.deleteFileRecursive(new File(impDirPath + "Upstream/"));
		LocalUtil.deleteFileRecursive(new File(impDirPath + "Buyer/"));

		FileListReader reader = LocalUtil.getDefaultReader();
		List<String> fileList = reader.readFileList(getPackageDir(), getRFCName());
		if (fileList != null) {
			for(String path : fileList){
				LocalUtil.copyfile(LocalUtil.getLocalPath(path.substring(path.indexOf("/") + 1), path.startsWith("Upstream")), impDirPath + path);
			}
		}
	}

	@Override
	public String getName() {
		return "Copiado de ficheros";
	}
}
