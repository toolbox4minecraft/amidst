package amidst.mojangapi;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

@Immutable
public class LauncherProfileRunner {
	private final WorldBuilder worldBuilder;
	private WorldOptions initialWorldOptions;

	public LauncherProfileRunner(WorldBuilder worldBuilder, String initialSeed, String initialWorldType) {
		this.worldBuilder = worldBuilder;
		this.initialWorldOptions = getInitialWorldOptions(initialSeed, initialWorldType);
	}

	private WorldOptions getInitialWorldOptions(String initialSeed, String initialWorldType) {
		if (initialSeed == null || initialSeed.isEmpty()) {
			if (initialWorldType != null) {
				AmidstLogger.warn("-world-type has no meaning without -seed");
			}
			return null;
		}

		WorldType worldType = WorldType.DEFAULT;
		if (initialWorldType != null && !initialWorldType.isEmpty()) {
			worldType = WorldType.findInstance(initialWorldType);
			if (worldType == null) {
				AmidstLogger.warn("Invalid value for -world-type: '" + initialWorldType + "', falling back to default");
				worldType = WorldType.DEFAULT;
			}
		}
		return new WorldOptions(WorldSeed.fromUserInput(initialSeed), worldType);
	}

	public RunningLauncherProfile run(LauncherProfile launcherProfile) throws MinecraftInterfaceCreationException {
		return RunningLauncherProfile.from(worldBuilder, launcherProfile, initialWorldOptions);
	}
}
