package ui.util;

import java.io.File;
import java.io.FileFilter;

public class DirectoryWithProjectFileFilter implements FileFilter {
	
	public boolean accept(File pathname) {
		if(!pathname.isDirectory() || !pathname.canRead())
			return false;
		
		ProjectFileFilter pff = new ProjectFileFilter();
		
		File res[] = pathname.listFiles(pff);
		if (res == null)
			return false;
		
		return res.length > 0;
	}
}
