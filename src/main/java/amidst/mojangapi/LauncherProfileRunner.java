package amidst.mojangapi;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.WorldBuilder;

@Immutable
public class LauncherProfileRunner {
	private final WorldBuilder worldBuilder;
	private String initialSeed;

	public LauncherProfileRunner(WorldBuilder worldBuilder, String initialSeed) {
		this.worldBuilder = worldBuilder;
		this.initialSeed = initialSeed;
	}

	public RunningLauncherProfile run(LauncherProfile launcherProfile) throws MinecraftInterfaceCreationException {
		return RunningLauncherProfile.from(worldBuilder, launcherProfile, initialSeed);
	}
}
