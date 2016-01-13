package amidst.devtools.settings;

public enum DevToolsSettings {
	INSTANCE;

	private String minecraftVersionsDirectory = "/home/stefan/.minecraft/amidst-all-client-versions/";
	private String minecraftLibrariesDirectory = "/home/stefan/.minecraft/libraries/";

	public String getMinecraftVersionsDirectory() {
		return minecraftVersionsDirectory;
	}

	public String getMinecraftLibrariesDirectory() {
		return minecraftLibrariesDirectory;
	}
}
