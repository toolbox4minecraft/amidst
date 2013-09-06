package amidst.json;

import java.io.File;

import javax.swing.JOptionPane;

import amidst.Log;
import amidst.Util;

public class InstallInformation {
	public String name;
	public String lastVersionId;
	public String gameDir;
	public String javaDir;
	public String javaArgs;
	public Resolution resolution;
	public String[] allowedReleaseTypes;
	public boolean isOld;
	
	public InstallInformation() {
		this(false);
	}
	
	public InstallInformation(boolean old) {
		if (!old) {
			name = "(Default)";
			lastVersionId = "1.6.2";
		} else {
			name = "Minecraft";
			lastVersionId = "None";
		}
		gameDir = Util.minecraftDirectory.toString();
		isOld = old;
	}
	
	public String toString() {
		return name;
	}
	
	public File getJarFile() {
		File returnFile;
		if (!isOld) {
			returnFile = new File(gameDir + "/versions/" + lastVersionId + "/" + lastVersionId + ".jar");
			if (returnFile.exists())
				return returnFile;
			File versionsPath = new File(gameDir + "/versions/");
			if (versionsPath.exists()) {
				File[] files = versionsPath.listFiles();
				for (int i = 0; i < files.length; i++) {
					File jar = new File(files[i] + "/" + files[i].getName() + ".jar");
					if (jar.exists())
						return jar;
				}
			}
		}
		returnFile = new File(Util.minecraftDirectory.toString() + "/bin/minecraft.jar");
		if (returnFile.exists())
			return returnFile;
		
		Log.kill("Found profile selection, but unable to locate minecraft.jar.");
		System.exit(0);
		return null;
	}
}
