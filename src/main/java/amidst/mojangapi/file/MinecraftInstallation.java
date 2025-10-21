package amidst.mojangapi.file;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.mojangapi.file.service.SaveDirectoryService;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Immutable
public class MinecraftInstallation {
	public static MinecraftInstallation newCustomMinecraftInstallation(
			Path libraries,
			Path versions,
			Path launcherProfilesJson) throws DotMinecraftDirectoryNotFoundException {
		DotMinecraftDirectory dotMinecraftDirectory = new DotMinecraftDirectory(DotMinecraftDirectory.getMinecraftDirectory(), libraries, versions, launcherProfilesJson);
		AmidstLogger.info("using '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "'");
		return new MinecraftInstallation(dotMinecraftDirectory);
	}

	public static MinecraftInstallation newLocalMinecraftInstallation(Path dotMinecraftDirectory2) {
		DotMinecraftDirectory dotMinecraftDirectory = DotMinecraftDirectory
				.createDotMinecraftDirectory(dotMinecraftDirectory2);
		AmidstLogger.info("using '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "'");
		return new MinecraftInstallation(dotMinecraftDirectory);
	}

	private final SaveDirectoryService saveDirectoryService = new SaveDirectoryService();
	private final DotMinecraftDirectory dotMinecraftDirectory;

	public MinecraftInstallation(DotMinecraftDirectory dotMinecraftDirectory) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
	}

	public List<LauncherProfile> readInstalledVersionsAsLauncherProfiles() throws FormatException {
		Path versions = dotMinecraftDirectory.getVersions();
		if (!Files.isDirectory(versions)) {
			return new ArrayList<>();
		}

		try {
			List<VersionDirectory> installedValidVersionDirectories = Files
					.list(versions)
					.filter(Files::isDirectory)
					.map(Path::getFileName)
					.map(id -> DotMinecraftDirectory.createVersionDirectory(dotMinecraftDirectory, id.toString()))
					.filter(VersionDirectory::isValid)
					.toList();

			List<LauncherProfile> result = new LinkedList<>();
			for (VersionDirectory versionDirectory : installedValidVersionDirectories) {
				result.add(newLauncherProfile(versionDirectory));
			}
			return result;
		} catch (IOException e) {
			AmidstLogger.error(e, "Error while reading directory " + versions);
			return new ArrayList<>();
		}
	}

	public List<UnresolvedLauncherProfile> readLauncherProfiles() throws FormatException, IOException {
		return JsonReader.readLocation(dotMinecraftDirectory.getLauncherProfilesJson(), LauncherProfilesJson.class)
				.getProfiles()
				.values()
				.stream()
				.map(p -> new UnresolvedLauncherProfile(dotMinecraftDirectory, p))
				.collect(Collectors.toList());
	}

	public LauncherProfile newLauncherProfile(String versionId) throws FormatException, IOException {
		return newLauncherProfile(
				DotMinecraftDirectory.createValidVersionDirectory(dotMinecraftDirectory, versionId));
	}

	private LauncherProfile newLauncherProfile(VersionDirectory versionDirectory) throws FormatException, IOException {
		VersionJson versionJson = JsonReader.readLocation(versionDirectory.getJson(), VersionJson.class);
		return new LauncherProfile(
				dotMinecraftDirectory,
				dotMinecraftDirectory.asProfileDirectory(),
				versionDirectory,
				versionJson,
				false,
				versionJson.getId());
	}

	public SaveGame newSaveGame(Path location) throws IOException, FormatException {
		SaveDirectory saveDirectory = saveDirectoryService.newSaveDirectory(location);
		return new SaveGame(saveDirectory, saveDirectoryService.readLevelDat(saveDirectory));
	}

	public Optional<LauncherProfile> tryReadLauncherProfile(
			Path minecraftJarFile,
			Path minecraftJsonFile) {
		if (minecraftJarFile == null || minecraftJsonFile == null) {
			return Optional.empty();
		}

		VersionDirectory versionDirectory = new VersionDirectory(minecraftJarFile, minecraftJsonFile);
		if (!versionDirectory.isValid()) {
			AmidstLogger.error("Cannot read launcher profile. minecraftJarFile: '" + minecraftJarFile + "', minecraftJsonFile: '" + minecraftJsonFile + "'");
			return Optional.empty();
		}

		try {
			return Optional.of(newLauncherProfile(versionDirectory));
		} catch (FormatException | IOException e) {
			AmidstLogger.error(
					e,
					"cannot read launcher profile. minecraftJarFile: '" + minecraftJarFile
							+ "', minecraftJsonFile: '" + minecraftJsonFile + "'");
			return Optional.empty();
		}
	}

	/**
	 * Try to locate a local profile with a name that matches the given string. No remote profiles are
	 * supported. Any errors are ignored and will result in a non-match.
	 */
	public Optional<LauncherProfile> tryGetLauncherProfileFromName(String profileName) {
		try {
			List<Version> versionList = Version.newLocalVersionList();
			for (UnresolvedLauncherProfile unresolvedProfile : readLauncherProfiles()) {
				LauncherProfile profile = unresolvedProfile.resolveToVanilla(versionList);
				if (profile.getProfileName().equalsIgnoreCase(profileName)) {
					return Optional.of(profile);
				}
			}
		} catch (FormatException | IOException e) {
			AmidstLogger.error(e, "error while reading launcher profiles");
		}

		return Optional.empty();
	}
}
