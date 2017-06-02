package amidst.mojangapi;

import java.io.IOException;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.SaveGame;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.minecraftinterface.local.DefaultClassTranslator;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterface;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class RunningLauncherProfile {
	public static RunningLauncherProfile from(WorldBuilder worldBuilder, LauncherProfile launcherProfile)
			throws LocalMinecraftInterfaceCreationException {
		return new RunningLauncherProfile(
				worldBuilder,
				launcherProfile,
				LocalMinecraftInterface.create(DefaultClassTranslator.INSTANCE.get(), launcherProfile));
	}

	private final WorldBuilder worldBuilder;
	private final LauncherProfile launcherProfile;
	private final MinecraftInterface minecraftInterface;
	private volatile World currentWorld = null;

	public RunningLauncherProfile(
			WorldBuilder worldBuilder,
			LauncherProfile launcherProfile,
			MinecraftInterface minecraftInterface) {
		this.worldBuilder = worldBuilder;
		this.launcherProfile = launcherProfile;
		this.minecraftInterface = minecraftInterface;
	}

	public LauncherProfile getLauncherProfile() {
		return launcherProfile;
	}

	public RecognisedVersion getRecognisedVersion() {
		return minecraftInterface.getRecognisedVersion();
	}

	public RunningLauncherProfile createSilentPlayerlessCopy() {
		try {
			return RunningLauncherProfile.from(WorldBuilder.createSilentPlayerless(), launcherProfile);
		} catch (LocalMinecraftInterfaceCreationException e) {
			// This will not happen normally, because we already successfully
			// created the same LocalMinecraftInterface once before.
			throw new RuntimeException("exception while duplicating the RunningLauncherProfile", e);
		}
	}

	/**
	 * Due to the limitation of the minecraft interface, you can only work with
	 * one world at a time. Creating a new world will break all previously
	 * created world objects.
	 */
	public synchronized World createWorldFromSeed(WorldSeed worldSeed, WorldType worldType)
			throws IllegalStateException,
			MinecraftInterfaceException {
		if (currentWorld == null) {
			currentWorld = worldBuilder.fromSeed(minecraftInterface, this::unlock, worldSeed, worldType);
			return currentWorld;
		} else {
			throw new IllegalStateException(
					"Each minecraft interface can only handle one world at a time. Dispose the previous world before creating a new one.");
		}
	}

	/**
	 * Due to the limitation of the minecraft interface, you can only work with
	 * one world at a time. Creating a new world will break all previously
	 * created world objects.
	 */
	public synchronized World createWorldFromSaveGame(SaveGame saveGame)
			throws IllegalStateException,
			IOException,
			MinecraftInterfaceException {
		if (currentWorld == null) {
			currentWorld = worldBuilder.fromSaveGame(minecraftInterface, this::unlock, saveGame);
			return currentWorld;
		} else {
			throw new IllegalStateException(
					"Each minecraft interface can only handle one world at a time. Dispose the previous world before creating a new one.");
		}
	}

	private synchronized void unlock(World world) throws IllegalStateException {
		if (currentWorld == world) {
			currentWorld = null;
		} else {
			throw new IllegalStateException("The requested world is no longer the currentWorld.");
		}
	}
}
