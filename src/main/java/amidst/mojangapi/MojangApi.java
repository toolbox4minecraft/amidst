package amidst.mojangapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.file.FilenameFactory;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.minecraftinterface.local.LocalMinecraftInterfaceCreationException;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

@ThreadSafe
public class MojangApi {
	private static final String UNKNOWN_VERSION_ID = "unknown";
	private static final String UNKNOWN_PROFILE_NAME = "unknown";

	private final WorldBuilder worldBuilder;
	private final DotMinecraftDirectory dotMinecraftDirectory;

	private volatile VersionListJson versionList;
	private volatile ProfileDirectory profileDirectory;
	private volatile VersionDirectory versionDirectory;
	private volatile MinecraftInterface minecraftInterface;
	private volatile String profileName;

	public MojangApi(WorldBuilder worldBuilder, DotMinecraftDirectory dotMinecraftDirectory) {
		this.worldBuilder = worldBuilder;
		this.dotMinecraftDirectory = dotMinecraftDirectory;
	}

	public DotMinecraftDirectory getDotMinecraftDirectory() {
		return dotMinecraftDirectory;
	}

	@NotNull
	public VersionListJson getVersionList() throws FileNotFoundException {
		VersionListJson versionList = this.versionList;
		if (versionList == null) {
			synchronized (this) {
				versionList = this.versionList;
				if (versionList == null) {
					versionList = JsonReader.readRemoteOrLocalVersionList();
					this.versionList = versionList;
				}
			}
		}
		return versionList;
	}

	public void set(String profileName, ProfileDirectory profileDirectory, VersionDirectory versionDirectory)
			throws LocalMinecraftInterfaceCreationException {
		this.profileName = profileName;
		this.profileDirectory = profileDirectory;
		this.versionDirectory = versionDirectory;
		if (versionDirectory != null) {
			try {
				this.minecraftInterface = versionDirectory.createLocalMinecraftInterface();
			} catch (LocalMinecraftInterfaceCreationException e) {
				this.minecraftInterface = null;
				throw e;
			}
		} else {
			this.minecraftInterface = null;
		}
	}

	public VersionDirectory createVersionDirectory(String versionId) {
		File versions = dotMinecraftDirectory.getVersions();
		File jar = FilenameFactory.getClientJarFile(versions, versionId);
		File json = FilenameFactory.getClientJsonFile(versions, versionId);
		return doCreateVersionDirectory(versionId, jar, json);
	}

	public VersionDirectory createVersionDirectory(File jar, File json) {
		return doCreateVersionDirectory(UNKNOWN_VERSION_ID, jar, json);
	}

	private VersionDirectory doCreateVersionDirectory(String versionId, File jar, File json) {
		return new VersionDirectory(dotMinecraftDirectory, versionId, jar, json);
	}

	public MojangApi createSilentPlayerlessCopy() {
		MojangApi result = new MojangApi(WorldBuilder.createSilentPlayerless(), dotMinecraftDirectory);
		try {
			result.set(profileName, profileDirectory, versionDirectory);
		} catch (LocalMinecraftInterfaceCreationException e) {
			// This will not happen normally, because we already successfully
			// created the same LocalMinecraftInterface once before.
			throw new RuntimeException("exception while duplicating the MojangApi", e);
		}
		return result;
	}

	public File getSaves() {
		ProfileDirectory profileDirectory = this.profileDirectory;
		if (profileDirectory != null) {
			return profileDirectory.getSaves();
		} else {
			return dotMinecraftDirectory.getSaves();
		}
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
			throws FileNotFoundException,
			IOException,
			IllegalStateException,
			MinecraftInterfaceException,
			MojangApiParsingException {
		MinecraftInterface minecraftInterface = this.minecraftInterface;
		if (minecraftInterface != null) {
			return worldBuilder.fromSaveGame(minecraftInterface, SaveDirectory.from(file));
		} else {
			throw new IllegalStateException("cannot create a world without a minecraft interface");
		}
	}

	public String getVersionId() {
		VersionDirectory versionDirectory = this.versionDirectory;
		if (versionDirectory != null) {
			return versionDirectory.getVersionId();
		} else {
			return UNKNOWN_VERSION_ID;
		}
	}

	public String getRecognisedVersionName() {
		MinecraftInterface minecraftInterface = this.minecraftInterface;
		if (minecraftInterface != null) {
			return minecraftInterface.getRecognisedVersion().getName();
		} else {
			return RecognisedVersion.UNKNOWN.getName();
		}
	}

	public String getProfileName() {
		String profileName = this.profileName;
		if (profileName != null) {
			return profileName;
		} else {
			return UNKNOWN_PROFILE_NAME;
		}
	}
}
