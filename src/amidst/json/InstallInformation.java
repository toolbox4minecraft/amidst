package amidst.json;

import java.io.File;

import amidst.Util;

public class InstallInformation {
	public String name;
	public String lastVersionId;
	public String gameDir;
	public String javaDir;
	public String javaArgs;
	public Resolution resolution;
	public String[] allowedReleaseTypes;
	public boolean isPre161;
	
	public InstallInformation() {
		this(false);
		// TODO Support various directories
	}
	
	public InstallInformation(boolean isOld) {
		if (!isOld) {
			name = "(Default)";
			lastVersionId = "1.6.2";
		} else {
			name = "Minecraft";
			lastVersionId = "None";
		}
		gameDir = Util.minecraftDirectory.toString();
		isPre161 = isOld;
	}
	
	public String toString() {
		return name;
	}
	
	public File getJarFile() {
		File returnFile = new File(gameDir + "/versions/" + lastVersionId + "/" + lastVersionId + ".jar");
		if (returnFile.exists())
			return returnFile;
		File versionsPath = new File(gameDir + "/versions/");
		File[] files = versionsPath.listFiles();
		for (int i = 0; i < files.length; i++) {
			File jar = new File(files[i] + "/" + files[i].getName() + ".jar");
			if (jar.exists())
				return jar;
		}
		return new File(Util.minecraftDirectory.toString() + "/bin/minecraft.jar");
	}
}
