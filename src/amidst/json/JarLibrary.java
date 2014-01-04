package amidst.json;

import java.io.File;
import java.util.ArrayList;

import amidst.Util;
import amidst.logging.Log;

public class JarLibrary {
	public String name;
	public ArrayList<JarRule> rules;
	
	private File file;
	
	public JarLibrary() {
		rules = new ArrayList<JarRule>();
	}
	
	public boolean isActive() {
		if (rules.size() == 0)
			return true;
		
		boolean isAllowed = false;
		for (JarRule rule : rules)
			if (rule.isApplicable())
				isAllowed = rule.isAllowed();
		
		return isAllowed;
	}
	
	public File getFile() {
		if (file == null) {
			String searchPath = Util.minecraftDirectory + "/libraries/";
			String[] pathSplit = name.split(":");
			pathSplit[0] = pathSplit[0].replace('.', '/');
			for (int i = 0; i < pathSplit.length; i++)
				searchPath += pathSplit[i] + "/";
			File searchPathFile = new File(searchPath);
			if (!searchPathFile.exists()) {
				Log.w("Failed attempt to load library at: " + searchPathFile);
				return null;
			}
			
			File[] libraryFiles = searchPathFile.listFiles();
			for (int i = 0; i < libraryFiles.length; i++) {
				String extension = "";
				String fileName = libraryFiles[i].getName();
				int q = fileName.lastIndexOf('.');
				if (q > 0)
					extension = fileName.substring(q+1);
				if (extension.equals("jar"))
					file = libraryFiles[i];
			}
			if (file == null)
				Log.w("Attempted to search for file at path: " + searchPath + " but found nothing. Skipping.");
		}
		return file;
	}
}
