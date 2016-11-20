package amidst.mojangapi;

import java.io.File;

import amidst.CommandLineParameters;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.Log;
import amidst.mojangapi.file.DotMinecraftDirectoryFinder;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.WorldBuilder;

@Immutable
public class MojangApiBuilder {
	private final WorldBuilder worldBuilder;
	private final CommandLineParameters parameters;

	public MojangApiBuilder(WorldBuilder worldBuilder, CommandLineParameters parameters) {
		this.worldBuilder = worldBuilder;
		this.parameters = parameters;
	}

	@NotNull
	public MojangApi construct()
			throws DotMinecraftDirectoryNotFoundException,
			LocalMinecraftInterfaceCreationException {
		DotMinecraftDirectory dotMinecraftDirectory = createDotMinecraftDirectory();
		if (dotMinecraftDirectory.isValid()) {
			Log
					.i(
							"using '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "', libraries: '"
									+ dotMinecraftDirectory.getLibraries() + "'");
		} else {
			throw new DotMinecraftDirectoryNotFoundException(
					"invalid '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "', libraries: '"
							+ dotMinecraftDirectory.getLibraries() + "'");
		}
		MojangApi result = new MojangApi(worldBuilder, dotMinecraftDirectory);
		result.set(null, null, createVersionDirectory(result));
		return result;
	}

	@NotNull
	private DotMinecraftDirectory createDotMinecraftDirectory() {
		File dotMinecraftDirectory = DotMinecraftDirectoryFinder.find(parameters.dotMinecraftDirectory);
		if (parameters.minecraftLibrariesDirectory != null) {
			return new DotMinecraftDirectory(dotMinecraftDirectory, new File(parameters.minecraftLibrariesDirectory));
		} else {
			return new DotMinecraftDirectory(dotMinecraftDirectory);
		}
	}

	private VersionDirectory createVersionDirectory(MojangApi mojangApi) {
		if (parameters.minecraftJarFile != null && parameters.minecraftJsonFile != null) {
			File jar = new File(parameters.minecraftJarFile);
			File json = new File(parameters.minecraftJsonFile);
			VersionDirectory result = mojangApi.createVersionDirectory(jar, json);
			if (result.isValid()) {
				Log
						.i(
								"using minecraft version directory. versionId: '" + result.getVersionId()
										+ "', jar file: '" + result.getJar() + "', json file: '" + result.getJson()
										+ "'");
				return result;
			} else {
				Log
						.w(
								"invalid minecraft version directory. versionId: '" + result.getVersionId()
										+ "', jar file: '" + result.getJar() + "', json file: '" + result.getJson()
										+ "'");
			}
		}
		return null;
	}
}
