package amidst.mojangapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.mojangapi.file.FilenameFactory;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.Worlds;

public class MojangApi {
	private static final String UNKNOWN_VERSION_ID = "unknown";

	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final VersionListJson versionList;
	private final File preferedJson;

	private volatile ProfileDirectory profileDirectory;
	private volatile MinecraftInterface minecraftInterface;

	public MojangApi(DotMinecraftDirectory dotMinecraftDirectory,
			VersionListJson versionList, File preferedJson) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
		this.versionList = versionList;
		this.preferedJson = preferedJson;
	}

	public DotMinecraftDirectory getDotMinecraftDirectory() {
		return dotMinecraftDirectory;
	}

	public VersionListJson getVersionList() {
		return versionList;
	}

	public void set(ProfileDirectory profileDirectory,
			VersionDirectory versionDirectory) {
		this.profileDirectory = profileDirectory;
		if (versionDirectory != null) {
			this.minecraftInterface = versionDirectory
					.createLocalMinecraftInterface();
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

	private VersionDirectory doCreateVersionDirectory(String versionId,
			File jar, File json) {
		if (preferedJson != null) {
			return new VersionDirectory(dotMinecraftDirectory, versionId, jar,
					preferedJson);
		} else {
			return new VersionDirectory(dotMinecraftDirectory, versionId, jar,
					json);
		}
	}

	public File getSaves() {
		if (profileDirectory != null) {
			return profileDirectory.getSaves();
		} else {
			return dotMinecraftDirectory.getSaves();
		}
	}

	public boolean canCreateWorld() {
		return minecraftInterface != null;
	}

	public World createRandomWorld(WorldType worldType)
			throws IllegalStateException {
		if (canCreateWorld()) {
			return Worlds.random(minecraftInterface, worldType);
		} else {
			throw new IllegalStateException(
					"cannot create a world without a minecraft interface");
		}
	}

	public World createWorldFromSeed(String seedText, WorldType worldType)
			throws IllegalStateException {
		if (canCreateWorld()) {
			return Worlds.fromSeed(minecraftInterface, seedText, worldType);
		} else {
			throw new IllegalStateException(
					"cannot create a world without a minecraft interface");
		}
	}

	public World createWorldFromFile(File file) throws FileNotFoundException,
			IOException, IllegalStateException {
		if (canCreateWorld()) {
			return Worlds
					.fromFile(minecraftInterface, SaveDirectory.from(file));
		} else {
			throw new IllegalStateException(
					"cannot create a world without a minecraft interface");
		}
	}

	public String getRecognisedVersionName() {
		if (canCreateWorld()) {
			return minecraftInterface.getRecognisedVersion().getName();
		} else {
			return RecognisedVersion.UNKNOWN.getName();
		}
	}
}
