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

	public ProfileDirectory getProfileDirectory() {
		return profileDirectory;
	}

	public VersionDirectory getVersionDirectory() {
		return versionDirectory;
	}

	public boolean hasProfileDirectory() {
		return profileDirectory != null;
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
			if (preferedJson != null) {
				return new Minecraft(versionDirectory.getJar(), preferedJson)
						.createInterface();
			} else {
				return new Minecraft(versionDirectory.getJar(),
						versionDirectory.getJson()).createInterface();
			}
		} catch (MalformedURLException e) {
			Log.crash(e, "MalformedURLException on Minecraft load.");
			return null;
		}
	}
}
