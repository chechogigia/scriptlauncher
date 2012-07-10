package ui.util;

import java.io.File;
import java.io.FilenameFilter;

public class ProjectFileFilter implements FilenameFilter {
	
	public boolean accept(File dir, String name) {
		return name.endsWith(".prj");
	}
}