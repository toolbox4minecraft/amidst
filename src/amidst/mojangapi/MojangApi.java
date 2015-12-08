package amidst.mojangapi;

import java.io.File;

import amidst.minecraft.IMinecraftInterface;
import amidst.minecraft.local.LocalMinecraftInterfaceBuilder;
import amidst.minecraft.world.World;
import amidst.minecraft.world.WorldType;
import amidst.minecraft.world.Worlds;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.versionlist.VersionListJson;

public class MojangApi {
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

	public void set(ProfileDirectory profileDirectory,
			VersionDirectory versionDirectory) {
		this.profileDirectory = profileDirectory;
		this.versionDirectory = versionDirectory;
	}

	@Deprecated
	public VersionDirectory getVersionDirectory() {
		if (preferedJson != null) {
			return new VersionDirectory(versionDirectory.getJar(), preferedJson);
		} else {
			return versionDirectory;
		}
	}

	public boolean hasVersionDirectory() {
		return versionDirectory != null;
	}

	public File getSaves() {
		if (profileDirectory != null) {
			return profileDirectory.getSaves();
		} else {
			return dotMinecraftDirectory.getSaves();
		}
	}

	@Deprecated
	public void createAndSetLocalMinecraftInterface() {
		this.minecraftInterface = createLocalMinecraftInterface();
	}

	private IMinecraftInterface createLocalMinecraftInterface() {
		return new LocalMinecraftInterfaceBuilder(dotMinecraftDirectory,
				getVersionDirectory()).create();
	}

	public DotMinecraftDirectory getDotMinecraftDirectory() {
		return dotMinecraftDirectory;
	}

	public VersionListJson getVersionList() {
		return versionList;
	}

	public World createRandomWorld(WorldType worldType) {
		return Worlds.random(minecraftInterface, worldType);
	}

	public World createWorldFromSeed(String seedText, WorldType worldType) {
		return Worlds.fromSeed(minecraftInterface, seedText, worldType);
	}

	public World createWorldFromFile(File worldFile) throws Exception {
		return Worlds.fromFile(minecraftInterface, worldFile);
	}

	public String getRecognisedVersionName() {
		return minecraftInterface.getRecognisedVersion().getName();
	}
}
