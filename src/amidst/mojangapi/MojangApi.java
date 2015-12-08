package amidst.mojangapi;

import java.io.File;

import amidst.minecraft.IMinecraftInterface;
import amidst.minecraft.RecognisedVersion;
import amidst.minecraft.local.LocalMinecraftInterfaceBuilder;
import amidst.minecraft.world.World;
import amidst.minecraft.world.WorldType;
import amidst.minecraft.world.Worlds;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.internal.FilenameFactory;
import amidst.mojangapi.versionlist.VersionListJson;

public class MojangApi {
	private static final String UNKNOWN_VERSION_ID = "unknown";

	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final VersionListJson versionList;
	private final File preferedJson;

	private volatile ProfileDirectory profileDirectory;
	private volatile VersionDirectory versionDirectory;
	private volatile IMinecraftInterface minecraftInterface;

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
		this.versionDirectory = versionDirectory;
		if (versionDirectory != null) {
			this.minecraftInterface = createLocalMinecraftInterface();
		} else {
			this.minecraftInterface = null;
		}
	}

	private IMinecraftInterface createLocalMinecraftInterface() {
		return new LocalMinecraftInterfaceBuilder(versionDirectory).create();
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

	public World createRandomWorld(WorldType worldType) {
		if (canCreateWorld()) {
			return Worlds.random(minecraftInterface, worldType);
		} else {
			throw new IllegalStateException(
					"cannot create a world without a minecraft interface");
		}
	}

	public World createWorldFromSeed(String seedText, WorldType worldType) {
		if (canCreateWorld()) {
			return Worlds.fromSeed(minecraftInterface, seedText, worldType);
		} else {
			throw new IllegalStateException(
					"cannot create a world without a minecraft interface");
		}
	}

	public World createWorldFromFile(File worldFile) throws Exception {
		if (canCreateWorld()) {
			return Worlds.fromFile(minecraftInterface, worldFile);
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
