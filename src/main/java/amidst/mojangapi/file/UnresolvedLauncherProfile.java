package amidst.mojangapi.file;

import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.mojangapi.file.service.DotMinecraftDirectoryService;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

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

	public LauncherProfile resolve(VersionList versionList) throws MojangApiParsingException, IOException {
		DotMinecraftDirectoryService dotMinecraftDirectoryService = new DotMinecraftDirectoryService();
		ProfileDirectory profileDirectory = dotMinecraftDirectoryService
				.createValidProfileDirectory(launcherProfileJson, dotMinecraftDirectory);
		VersionDirectory versionDirectory = dotMinecraftDirectoryService
				.createValidVersionDirectory(launcherProfileJson, versionList, dotMinecraftDirectory);
		try {
			VersionJson versionJson = JsonReader.readLocation(versionDirectory.getJson(), VersionJson.class);
			return new LauncherProfile(
					dotMinecraftDirectory,
					profileDirectory,
					versionDirectory,
					versionJson,
					launcherProfileJson.getName());
		} catch (FormatException e) {
			throw new MojangApiParsingException(e);
		}
	}
}
