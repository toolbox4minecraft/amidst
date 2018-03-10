package amidst.minetest.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import amidst.documentation.Immutable;
import amidst.logging.AmidstLogger;
import amidst.gameengineabstraction.file.IUnresolvedLauncherProfile;
import amidst.gameengineabstraction.file.IGameInstallation;
import amidst.minetest.MinetestLauncherProfile;
import amidst.minetest.file.directory.MinetestDirectory;
import amidst.minetest.file.service.MinetestDirectoryService;
import amidst.minetest.file.UnresolvedLauncherProfile;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.SaveGame;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.mojangapi.file.service.DotMinecraftDirectoryService;
import amidst.mojangapi.file.service.SaveDirectoryService;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class MinetestInstallation implements IGameInstallation {
	
	public static MinetestInstallation newLocalMinetestInstallation() throws DotMinecraftDirectoryNotFoundException {
		return newLocalMinetestInstallationOrDefault(null);
	}

	public static MinetestInstallation newLocalMinetestInstallationOrDefault(String preferredMinetestDirectory) {
		
		MinetestDirectory minetestDirectory = null;
		try {
			minetestDirectory = new MinetestDirectoryService()
					.createMinetestDirectory(preferredMinetestDirectory);
			AmidstLogger.info("using minetest directory at: '" + minetestDirectory.getRoot() + "'");
		} catch (DotMinecraftDirectoryNotFoundException e) {
						
			AmidstLogger.info("Minetest directory not found, using default mapgen v7 profile");
		}
		return new MinetestInstallation(minetestDirectory);
	}

	private final MinetestDirectoryService minetestDirectoryService = new MinetestDirectoryService();
	private final MinetestDirectory minetestDirectory;

	public MinetestInstallation(MinetestDirectory minetestDirectory) {
		this.minetestDirectory = minetestDirectory;
	}

	
	public Optional<LauncherProfile> defaultLauncherProfile() {
		
		return Optional.of(MinetestLauncherProfile.InternalDefault);
	}
	
	public SaveGame newSaveGame(File location) throws IOException, FormatException {
		//SaveDirectory saveDirectory = saveDirectoryService.newSaveDirectory(location);
		//return new SaveGame(saveDirectory, saveDirectoryService.readLevelDat(saveDirectory));
		throw new UnsupportedOperationException("need to add support for world saves");
	}

	@Override
	public List<IUnresolvedLauncherProfile> readLauncherProfiles()
			throws FormatException, IOException {
		// TODO Auto-generated method stub
		
		ArrayList<IUnresolvedLauncherProfile> result = new ArrayList<IUnresolvedLauncherProfile>();
		result.add(new UnresolvedLauncherProfile());
		return result;
	}	
	
	/*
	public List<LauncherProfile> readInstalledVersionsAsLauncherProfiles() throws FormatException, IOException {
		List<LauncherProfile> result = new LinkedList<>();
		for (VersionDirectory versionDirectory : minetestDirectoryService
				.findInstalledValidVersionDirectories(minetestDirectory)) {
			result.add(newLauncherProfile(versionDirectory));
		}
		return result;
	}

	public List<UnresolvedLauncherProfile> readLauncherProfiles() throws FormatException, IOException {
		return minetestDirectoryService
				.readLauncherProfilesFrom(minetestDirectory)
				.getProfiles()
				.values()
				.stream()
				.map(p -> new UnresolvedLauncherProfile(minetestDirectory, p))
				.collect(Collectors.toList());
	}

	public LauncherProfile newLauncherProfile(String versionId) throws FormatException, IOException {
		return newLauncherProfile(
				minetestDirectoryService.createValidVersionDirectory(minetestDirectory, versionId));
	}

	public LauncherProfile newLauncherProfile(File jar, File json) throws FormatException, IOException {
		return newLauncherProfile(minetestDirectoryService.createValidVersionDirectory(jar, json));
	}

	private LauncherProfile newLauncherProfile(VersionDirectory versionDirectory) throws FormatException, IOException {
		VersionJson versionJson = JsonReader.readLocation(versionDirectory.getJson(), VersionJson.class);
		return new LauncherProfile(
				minetestDirectory,
				minetestDirectory.asProfileDirectory(),
				versionDirectory,
				versionJson,
				false,
				versionJson.getId());
	}

	public SaveGame newSaveGame(File location) throws IOException, FormatException {
		SaveDirectory saveDirectory = saveDirectoryService.newSaveDirectory(location);
		return new SaveGame(saveDirectory, saveDirectoryService.readLevelDat(saveDirectory));
	}

	public Optional<LauncherProfile> tryReadLauncherProfile(
			String preferredMinecraftJarFile,
			String preferredMinecraftJsonFile) {
		if (preferredMinecraftJarFile != null && preferredMinecraftJsonFile != null) {
			try {
				return Optional.of(
						newLauncherProfile(new File(preferredMinecraftJarFile), new File(preferredMinecraftJsonFile)));
			} catch (FormatException | IOException e) {
				AmidstLogger.error(
						e,
						"cannot read launcher profile. preferredMinecraftJarFile: '" + preferredMinecraftJarFile
								+ "', preferredMinecraftJsonFile: '" + "'");
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}
	*/
}
