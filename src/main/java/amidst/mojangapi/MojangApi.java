package amidst.mojangapi;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
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
import amidst.parsing.FormatException;

@ThreadSafe
public class MojangApi {
	private final WorldBuilder worldBuilder;
	private final MinecraftInstallation minecraftInstallation;

	private volatile MinecraftInterface minecraftInterface;
	private volatile LauncherProfile launcherProfile;

	public MojangApi(WorldBuilder worldBuilder, MinecraftInstallation minecraftInstallation) {
		this.worldBuilder = worldBuilder;
		this.minecraftInstallation = minecraftInstallation;
	}

	public MinecraftInstallation getMinecraftInstallation() {
		return minecraftInstallation;
	}

	public Optional<LauncherProfile> getLauncherProfile() {
		return Optional.ofNullable(launcherProfile);
	}

	public void setLauncherProfile(LauncherProfile launcherProfile) throws LocalMinecraftInterfaceCreationException {
		this.launcherProfile = launcherProfile;
		if (launcherProfile != null) {
			AmidstLogger.info(
					"using launcher profile. version id: '" + launcherProfile.getVersionId() + "', profile name: '"
							+ launcherProfile.getProfileName() + "', jar file: '" + launcherProfile.getJar() + "'");
			try {
				this.minecraftInterface = LocalMinecraftInterface
						.create(DefaultClassTranslator.INSTANCE.get(), launcherProfile);
			} catch (LocalMinecraftInterfaceCreationException e) {
				this.minecraftInterface = null;
				throw e;
			}
		} else {
			this.minecraftInterface = null;
		}
	}

	public MojangApi createSilentPlayerlessCopy() {
		MojangApi result = new MojangApi(WorldBuilder.createSilentPlayerless(), minecraftInstallation);
		try {
			result.setLauncherProfile(launcherProfile);
		} catch (LocalMinecraftInterfaceCreationException e) {
			// This will not happen normally, because we already successfully
			// created the same LocalMinecraftInterface once before.
			throw new RuntimeException("exception while duplicating the MojangApi", e);
		}
		return result;
	}

	public boolean canCreateWorld() {
		return minecraftInterface != null;
	}

	/**
	 * Due to the limitation of the minecraft interface, you can only work with
	 * one world at a time. Creating a new world will break all previously
	 * created world objects.
	 */
	public World createWorldFromSeed(WorldSeed worldSeed, WorldType worldType)
			throws IllegalStateException,
			MinecraftInterfaceException {
		MinecraftInterface minecraftInterface = this.minecraftInterface;
		if (minecraftInterface != null) {
			return worldBuilder.fromSeed(minecraftInterface, worldSeed, worldType);
		} else {
			throw new IllegalStateException("cannot create a world without a minecraft interface");
		}
	}

	/**
	 * Due to the limitation of the minecraft interface, you can only work with
	 * one world at a time. Creating a new world will break all previously
	 * created world objects.
	 */
	public World createWorldFromSaveGame(File file)
			throws IllegalStateException,
			MinecraftInterfaceException,
			IOException,
			FormatException {
		MinecraftInterface minecraftInterface = this.minecraftInterface;
		if (minecraftInterface != null) {
			return worldBuilder.fromSaveGame(minecraftInterface, minecraftInstallation.newSaveGame(file));
		} else {
			throw new IllegalStateException("cannot create a world without a minecraft interface");
		}
	}

	public String getRecognisedVersionName() {
		return Optional
				.ofNullable(this.minecraftInterface)
				.map(MinecraftInterface::getRecognisedVersion)
				.map(RecognisedVersion::getName)
				.orElse(RecognisedVersion.UNKNOWN.getName());
	}
}
