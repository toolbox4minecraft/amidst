package amidst.devtools.settings;

public enum DevToolsSettings {
	INSTANCE;

	private String minecraftVersionsDirectory = "/tmp/mc/";

	public String getMinecraftVersionsDirectory() {
		return minecraftVersionsDirectory;
	}
}
