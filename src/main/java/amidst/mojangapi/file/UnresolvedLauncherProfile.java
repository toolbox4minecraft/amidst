package amidst.mojangapi.file;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

import java.io.IOException;
import java.util.List;

@Immutable
public class UnresolvedLauncherProfile {
	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final LauncherProfileJson launcherProfileJson;

	public UnresolvedLauncherProfile(
			DotMinecraftDirectory dotMinecraftDirectory,
			LauncherProfileJson launcherProfileJson) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
		this.launcherProfileJson = launcherProfileJson;
	}

	public String getName() {
		return launcherProfileJson.getName();
	}
	
	public String getIcon() {
		return launcherProfileJson.getIcon();
	}
	
	public String getVersionId() {
		return launcherProfileJson.getLastVersionId();
	}

	public LauncherProfile resolve(List<Version> versionList) throws FormatException, IOException {
		ProfileDirectory profileDirectory = DotMinecraftDirectory
				.createValidProfileDirectory(launcherProfileJson, dotMinecraftDirectory);
		VersionDirectory versionDirectory = DotMinecraftDirectory
				.createValidVersionDirectory(launcherProfileJson, versionList, dotMinecraftDirectory);
		VersionJson versionJson = JsonReader.readLocation(versionDirectory.getJson(), VersionJson.class);
		return new LauncherProfile(
				dotMinecraftDirectory,
				profileDirectory,
				versionDirectory,
				versionJson,
				true,
				launcherProfileJson.getName());
	}

	public LauncherProfile resolveToVanilla(List<Version> versionList) throws FormatException, IOException {
		ProfileDirectory profileDirectory = DotMinecraftDirectory
				.createValidProfileDirectory(launcherProfileJson, dotMinecraftDirectory);
		VersionDirectory versionDirectory = DotMinecraftDirectory
				.createValidVersionDirectory(launcherProfileJson, versionList, dotMinecraftDirectory);
		VersionJson versionJson = JsonReader.readLocation(versionDirectory.getJson(), VersionJson.class);
		boolean isVersionListedInProfile = true;
		while (versionJson.getInheritsFrom() != null) {
			versionDirectory = DotMinecraftDirectory
					.createValidVersionDirectory(dotMinecraftDirectory, versionJson.getInheritsFrom());
			versionJson = JsonReader.readLocation(versionDirectory.getJson(), VersionJson.class);
			isVersionListedInProfile = false;
		}
		return new LauncherProfile(
				dotMinecraftDirectory,
				profileDirectory,
				versionDirectory,
				versionJson,
				isVersionListedInProfile,
				launcherProfileJson.getName());
	}
}
