package amidst.preferences;

import java.io.File;

import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;

public class ProfileSelection {
	private final DotMinecraftDirectory dotMinecraftDirectory;

	private volatile ProfileDirectory profileDirectory;
	private volatile VersionDirectory versionDirectory;

	public ProfileSelection(DotMinecraftDirectory dotMinecraftDirectory,
			ProfileDirectory profileDirectory, VersionDirectory versionDirectory) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
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
}
