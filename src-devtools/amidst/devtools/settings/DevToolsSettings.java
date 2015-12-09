package amidst.devtools.settings;

public enum DevToolsSettings {
	INSTANCE;

	private String minecraftVersionsDirectory = "/home/stefan/.minecraft/amidst-all-client-versions/";

	public String getMinecraftVersionsDirectory() {
		return minecraftVersionsDirectory;
	}
}
