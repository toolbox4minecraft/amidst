package amidst.preferences;

import java.io.File;
import java.net.MalformedURLException;

import amidst.logging.Log;
import amidst.minecraft.IMinecraftInterface;
import amidst.minecraft.Minecraft;
import amidst.minecraft.MinecraftUtil;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;

public class ProfileSelection {
	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final File preferedJson;

	private volatile ProfileDirectory profileDirectory;
	private volatile VersionDirectory versionDirectory;

	public ProfileSelection(DotMinecraftDirectory dotMinecraftDirectory,
			File preferedJson, ProfileDirectory profileDirectory,
			VersionDirectory versionDirectory) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
		this.preferedJson = preferedJson;
		this.profileDirectory = profileDirectory;
		this.versionDirectory = versionDirectory;
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
		MinecraftUtil.setInterface(createLocalMinecraftInterface());
	}

	public IMinecraftInterface createLocalMinecraftInterface() {
		try {
			return new Minecraft(dotMinecraftDirectory, getVersionDirectory())
					.createInterface();
		} catch (MalformedURLException e) {
			Log.crash(e, "MalformedURLException on Minecraft load.");
			return null;
		}
	}
}
