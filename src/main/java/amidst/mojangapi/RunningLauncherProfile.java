package amidst.mojangapi;

import java.io.IOException;
import java.util.Optional;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.SaveGame;
import amidst.mojangapi.minecraftinterface.LoggingMinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaces;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldOptions;

@ThreadSafe
public class RunningLauncherProfile {
	public static RunningLauncherProfile from(WorldBuilder worldBuilder, LauncherProfile launcherProfile, Optional<WorldOptions> initialWorldOptions)
			throws MinecraftInterfaceCreationException {
		return new RunningLauncherProfile(
				worldBuilder,
				launcherProfile,
				new LoggingMinecraftInterface(MinecraftInterfaces.fromLocalProfile(launcherProfile)), initialWorldOptions);
	}

	private final WorldBuilder worldBuilder;
	private final LauncherProfile launcherProfile;
	private final MinecraftInterface minecraftInterface;
	private final Optional<WorldOptions> initialWorldOptions;

	public RunningLauncherProfile(
			WorldBuilder worldBuilder,
			LauncherProfile launcherProfile,
			MinecraftInterface minecraftInterface,
			Optional<WorldOptions> initialWorldOptions) {
		this.worldBuilder = worldBuilder;
		this.launcherProfile = launcherProfile;
		this.minecraftInterface = minecraftInterface;
		this.initialWorldOptions = initialWorldOptions;
	}

	public LauncherProfile getLauncherProfile() {
		return launcherProfile;
	}

	public Optional<WorldOptions> getInitialWorldOptions() {
		return initialWorldOptions;
	}

	public RecognisedVersion getRecognisedVersion() {
		return minecraftInterface.getRecognisedVersion();
	}

	public RunningLauncherProfile createSilentPlayerlessCopy() {
		try {
			return RunningLauncherProfile.from(WorldBuilder.createSilentPlayerless(), launcherProfile, null);
		} catch (MinecraftInterfaceCreationException e) {
			// This will not happen normally, because we already successfully
			// created the same LocalMinecraftInterface once before.
			throw new RuntimeException("exception while duplicating the RunningLauncherProfile", e);
		}
	}

	public synchronized World createWorld(WorldOptions worldOptions)
			throws MinecraftInterfaceException {
		return worldBuilder.from(minecraftInterface, worldOptions);
	}

	public synchronized World createWorldFromSaveGame(SaveGame saveGame)
			throws IOException, MinecraftInterfaceException {
		return worldBuilder.fromSaveGame(minecraftInterface, saveGame);
	}
}
