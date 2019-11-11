package amidst.mojangapi.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.mojangapi.file.service.DotMinecraftDirectoryService;
import amidst.mojangapi.file.service.SaveDirectoryService;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class MinecraftInstallation {
	public static MinecraftInstallation newCustomMinecraftInstallation(
			File libraries,
			File saves,
			File versions,
			File launcherProfilesJson) throws DotMinecraftDirectoryNotFoundException {
		DotMinecraftDirectory dotMinecraftDirectory = new DotMinecraftDirectoryService()
				.createCustomDotMinecraftDirectory(libraries, saves, versions, launcherProfilesJson);
		AmidstLogger.info("using '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "'");
		return new MinecraftInstallation(dotMinecraftDirectory);
	}

	public static MinecraftInstallation newLocalMinecraftInstallation() throws DotMinecraftDirectoryNotFoundException {
		return newLocalMinecraftInstallation(null);
	}

	public static MinecraftInstallation newLocalMinecraftInstallation(File dotMinecraftDirectory2)
			throws DotMinecraftDirectoryNotFoundException {
		DotMinecraftDirectory dotMinecraftDirectory = new DotMinecraftDirectoryService()
				.createDotMinecraftDirectory(dotMinecraftDirectory2);
		AmidstLogger.info("using '.minecraft' directory at: '" + dotMinecraftDirectory.getRoot() + "'");
		return new MinecraftInstallation(dotMinecraftDirectory);
	}

	private final SaveDirectoryService saveDirectoryService = new SaveDirectoryService();
	private final DotMinecraftDirectoryService dotMinecraftDirectoryService = new DotMinecraftDirectoryService();
	private final DotMinecraftDirectory dotMinecraftDirectory;

	public MinecraftInstallation(DotMinecraftDirectory dotMinecraftDirectory) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
	}

	public List<LauncherProfile> readInstalledVersionsAsLauncherProfiles() throws FormatException, IOException {
		List<LauncherProfile> result = new LinkedList<>();
		for (VersionDirectory versionDirectory : dotMinecraftDirectoryService
				.findInstalledValidVersionDirectories(dotMinecraftDirectory)) {
			result.add(newLauncherProfile(versionDirectory));
		}
		return result;
	}

	public List<UnresolvedLauncherProfile> readLauncherProfiles() throws FormatException, IOException {
		return dotMinecraftDirectoryService
				.readLauncherProfilesFrom(dotMinecraftDirectory)
				.getProfiles()
				.values()
				.stream()
				.map(p -> new UnresolvedLauncherProfile(dotMinecraftDirectory, p))
				.collect(Collectors.toList());
	}

	public LauncherProfile newLauncherProfile(String versionId) throws FormatException, IOException {
		return newLauncherProfile(
				dotMinecraftDirectoryService.createValidVersionDirectory(dotMinecraftDirectory, versionId));
	}

	public LauncherProfile newLauncherProfile(File jar, File json) throws FormatException, IOException {
		return newLauncherProfile(dotMinecraftDirectoryService.createValidVersionDirectory(jar, json));
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

	public SaveGame newSaveGame(File location) throws IOException, FormatException {
		SaveDirectory saveDirectory = saveDirectoryService.newSaveDirectory(location);
		return new SaveGame(saveDirectory, saveDirectoryService.readLevelDat(saveDirectory));
	}

/*
	public Optional<LauncherProfile> tryReadLauncherProfile(
<<<<<<< Updated upstream
			String preferredMinecraftJarFile,
			String preferredMinecraftJsonFile,
			String profileName) {
		if (preferredMinecraftJarFile != null && preferredMinecraftJsonFile != null) {
=======
			File minecraftJarFile,
			File minecraftJsonFile) {
		if (minecraftJarFile != null && minecraftJsonFile != null) {
>>>>>>> Stashed changes
			try {
				return Optional.of(
						newLauncherProfile(minecraftJarFile, minecraftJsonFile));
			} catch (FormatException | IOException e) {
				AmidstLogger.error(
						e,
<<<<<<< Updated upstream
						"cannot read launcher profile. preferredMinecraftJarFile: '" + preferredMinecraftJarFile
								+ "', preferredMinecraftJsonFile: '" + "'");
=======
						"cannot read launcher profile. preferredMinecraftJarFile: '" + minecraftJarFile
								+ "', preferredMinecraftJsonFile: '" + minecraftJsonFile + "'");
				return Optional.empty();
>>>>>>> Stashed changes
			}
		} else if (profileName != null) {
			LauncherProfile profile = newLauncherProfileFromName(profileName);
			if (profile != null) {
				AmidstLogger.info("Selecting profile with name '" + profile.getProfileName() + "'");
				return Optional.of(profile);
			} else {
				AmidstLogger.error("Cannot find profile matching name '" + profileName + "'");
			}
		}

		return Optional.empty();
	}
*/
    public Optional<LauncherProfile> tryReadLauncherProfile(
            File minecraftJarFile,
            File minecraftJsonFile) {
        if (minecraftJarFile != null && minecraftJsonFile != null) {
            try {
                return Optional.of(
                        newLauncherProfile(minecraftJarFile, minecraftJsonFile));
            } catch (FormatException | IOException e) {
                AmidstLogger.error(
                        e,
                        "cannot read launcher profile. minecraftJarFile: '" + minecraftJarFile
                                + "', minecraftJsonFile: '" + minecraftJsonFile + "'");
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * Try to locate a local profile with a name that matches the given string. No remote profiles are
     * supported. Any errors are ignored and will result in a non-match.
     */
    public Optional<LauncherProfile> tryGetLauncherProfileFromName(String profileName) {
        try {
            VersionList versionList = VersionList.newLocalVersionList();
            List<UnresolvedLauncherProfile> unresolvedProfiles = readLauncherProfiles();
            for (UnresolvedLauncherProfile unresolvedProfile : unresolvedProfiles) {
                LauncherProfile profile = unresolvedProfile.resolveToVanilla(versionList);
                if (profile.getProfileName().equalsIgnoreCase(profileName)) {
                    return Optional.of(profile);
                }
            }
        } catch(FormatException | IOException e) {
            AmidstLogger.error(e, "error while reading launcher profiles");
        }

        return Optional.empty();
    }
}
