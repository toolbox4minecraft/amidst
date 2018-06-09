package amidst.devtools.settings;

import java.util.Properties;

import amidst.ResourceLoader;
import amidst.documentation.Immutable;

@Immutable
public enum DevToolSettings {
	INSTANCE;

	private final Properties properties = ResourceLoader.getProperties("/amidst/devtools/devtools.properties");

	public String getMinecraftVersionsDirectory() {
		return properties.getProperty("amidst.devtools.directory.versions");
	}

	public String getMinecraftLibrariesDirectory() {
		return properties.getProperty("amidst.devtools.directory.libraries");
	}

	public String getBiomeColorImagesDirectory() {
		return properties.getProperty("amidst.devtools.directory.biome.color.images");
	}

	public String getBenchmarksDirectory() {
		return properties.getProperty("amidst.devtools.directory.benchmarks");
	}
}
