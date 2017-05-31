package amidst.mojangapi;

import java.io.File;
import java.io.IOException;

import amidst.CommandLineParameters;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.facade.MinecraftInstallation;
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
		MinecraftInstallation minecraftInstallation = MinecraftInstallation
				.newLocalMinecraftInstallation(parameters.dotMinecraftDirectory);
		MojangApi result = new MojangApi(worldBuilder, minecraftInstallation);
		if (parameters.minecraftJarFile != null && parameters.minecraftJsonFile != null) {
			try {
				result.setLauncherProfile(
						minecraftInstallation.newLauncherProfile(
								new File(parameters.minecraftJarFile),
								new File(parameters.minecraftJsonFile)));
			} catch (MojangApiParsingException | IOException e) {
				result.setLauncherProfile(null);
			}
		} else {
			result.setLauncherProfile(null);
		}
		return result;
	}
}
