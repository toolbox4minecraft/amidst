package amidst.mojangapi;

import java.util.Optional;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldOptions;

@Immutable
public class LauncherProfileRunner {
	private final WorldBuilder worldBuilder;
	private Optional<WorldOptions> initialWorldOptions;

	public LauncherProfileRunner(WorldBuilder worldBuilder, Optional<WorldOptions> initialWorldOptions) {
		this.worldBuilder = worldBuilder;
		this.initialWorldOptions = initialWorldOptions;
	}

	public RunningLauncherProfile run(LauncherProfile launcherProfile) throws MinecraftInterfaceCreationException {
		return RunningLauncherProfile.from(worldBuilder, launcherProfile, initialWorldOptions);
	}
}
