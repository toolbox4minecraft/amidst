package amidst.devtools.settings;

import java.util.Properties;

import amidst.ResourceLoader;
import amidst.documentation.Immutable;

@Immutable
public enum DevToolsSettings {
	INSTANCE;

	private final Properties properties = ResourceLoader
			.getProperties("/amidst/devtools/devtools.properties");

	public String getMinecraftVersionsDirectory() {
		return properties.getProperty("amidst.devtools.directory.versions");
	}

	public String getMinecraftLibrariesDirectory() {
		return properties.getProperty("amidst.devtools.directory.libraries");
	}
}
