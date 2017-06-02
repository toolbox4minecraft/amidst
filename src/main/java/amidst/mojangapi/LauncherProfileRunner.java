package amidst.mojangapi;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.WorldBuilder;

@Immutable
public class LauncherProfileRunner {
	private final WorldBuilder worldBuilder;

	public LauncherProfileRunner(WorldBuilder worldBuilder) {
		this.worldBuilder = worldBuilder;
	}

	public RunningLauncherProfile run(LauncherProfile launcherProfile) throws LocalMinecraftInterfaceCreationException {
		return RunningLauncherProfile.from(worldBuilder, launcherProfile);
	}
}
