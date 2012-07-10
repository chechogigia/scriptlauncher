package ui.util;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFileFilter implements FileFilter {
	
	public boolean accept(File pathname) {
		if(!pathname.isDirectory() || !pathname.canRead())
			return false;
		
		return true;
	}
}
