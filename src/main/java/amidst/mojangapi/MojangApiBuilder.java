package amidst.mojangapi;

import java.io.File;

import amidst.CommandLineParameters;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.service.DotMinecraftDirectoryService;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.WorldBuilder;

@Immutable
public class MojangApiBuilder {
	private final WorldBuilder worldBuilder;
	private final CommandLineParameters parameters;
	private final DotMinecraftDirectoryService dotMinecraftDirectoryService = new DotMinecraftDirectoryService();

	public MojangApiBuilder(WorldBuilder worldBuilder, CommandLineParameters parameters) {
		this.worldBuilder = worldBuilder;
		this.parameters = parameters;
	}

	@NotNull
	public MojangApi construct()
			throws DotMinecraftDirectoryNotFoundException,
			LocalMinecraftInterfaceCreationException {
		DotMinecraftDirectory dotMinecraftDirectory = dotMinecraftDirectoryService
				.createDotMinecraftDirectory(parameters.dotMinecraftDirectory, parameters.minecraftLibrariesDirectory);
		if (dotMinecraftDirectory.isValid()) {
			AmidstLogger.info(
					"using '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "', libraries: '"
							+ dotMinecraftDirectory.getLibraries() + "'");
		} else {
			throw new DotMinecraftDirectoryNotFoundException(
					"invalid '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "', libraries: '"
							+ dotMinecraftDirectory.getLibraries() + "'");
		}
		MojangApi result = new MojangApi(worldBuilder, dotMinecraftDirectory);
		result.set(null, null, createVersionDirectory());
		return result;
	}

	private VersionDirectory createVersionDirectory() {
		if (parameters.minecraftJarFile != null && parameters.minecraftJsonFile != null) {
			File jar = new File(parameters.minecraftJarFile);
			File json = new File(parameters.minecraftJsonFile);
			VersionDirectory result = dotMinecraftDirectoryService
					.createVersionDirectoryWithUnknownVersionId(jar, json);
			if (result.isValid()) {
				AmidstLogger.info(
						"using minecraft version directory. versionId: '" + result.getVersionId() + "', jar file: '"
								+ result.getJar() + "', json file: '" + result.getJson() + "'");
				return result;
			} else {
				AmidstLogger.warn(
						"invalid minecraft version directory. versionId: '" + result.getVersionId() + "', jar file: '"
								+ result.getJar() + "', json file: '" + result.getJson() + "'");
			}
		}
		return null;
	}
}
