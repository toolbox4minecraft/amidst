package amidst.devtools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.Classes;
import amidst.clazz.real.JarFileParsingException;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.translator.ClassTranslator;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaces;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.parsing.FormatException;

/**
 * This only checks if there is exactly one class in the jar file for each
 * class, defined by the class translator. Optional classes can be missing.
 * However, it does not check if the class has all the required constructors,
 * methods and fields. It also does not try to use the found classes.
 */
public class MinecraftVersionCompatibilityChecker {
	private final String prefix;
	private final VersionList versionList;
	private final MinecraftInstallation minecraftInstallation;

	public MinecraftVersionCompatibilityChecker(String prefix, String libraries, VersionList versionList)
			throws DotMinecraftDirectoryNotFoundException {
		this.prefix = prefix;
		this.versionList = versionList;
		this.minecraftInstallation = MinecraftInstallation
				.newCustomMinecraftInstallation(Paths.get(libraries), null, Paths.get(prefix), null);
	}

	public void run() {
		Map<VersionStatus, List<Version>> statuses = new EnumMap<>(VersionStatus.class);

		for (Version version : versionList.getVersions()) {
			statuses.computeIfAbsent(checkOne(version), k -> new ArrayList<>()).add(version);
		}

		displayVersionList("============== SUPPORTED VERSIONS ==============", statuses.get(VersionStatus.SUPPORTED));
		displayVersionList("============= UNSUPPORTED VERSIONS =============", statuses.get(VersionStatus.UNSUPPORTED));
		displayVersionList("============= UNRECOGNISED VERSIONS ============", statuses.get(VersionStatus.UNRECOGNISED));
		displayVersionList("================ FAILED VERSIONS ===============", statuses.get(VersionStatus.FAILED));
	}

	private VersionStatus checkOne(Version version) {
		try {
			version.fetchRemoteVersion().downloadClient(prefix);
		} catch (IOException | FormatException e) {
			return VersionStatus.UNSUPPORTED;
		}

		try {
			LauncherProfile launcherProfile = minecraftInstallation.newLauncherProfile(version.getId());
			RecognisedVersion recognisedVersion = RecognisedVersion.from(launcherProfile.newClassLoader());
			if(!recognisedVersion.isKnown())
				return VersionStatus.UNRECOGNISED;

			Path jarFile = version.getClientJarFile(Paths.get(prefix));
			ClassTranslator translator = MinecraftInterfaces.getClassTranslatorFromVersion(recognisedVersion);
			if(isSupported(Classes.countMatches(jarFile, translator)))
				return VersionStatus.SUPPORTED;
		} catch (JarFileParsingException | FormatException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return VersionStatus.FAILED;
		}

		return VersionStatus.UNSUPPORTED;
	}

	private boolean isSupported(Map<SymbolicClassDeclaration, Integer> matchesMap) {
		for (Entry<SymbolicClassDeclaration, Integer> entry : matchesMap.entrySet()) {
			if (entry.getValue() > 1) {
				return false;
			} else if (entry.getValue() == 0 && !entry.getKey().isOptional()) {
				return false;
			}
		}
		return true;
	}

	private void displayVersionList(String message, List<Version> versions) {
		System.out.println();
		System.out.println(message);
		for (Version version : versions) {
			System.out.println(version.getId());
		}
	}

	private static enum VersionStatus {
		SUPPORTED,
		UNSUPPORTED,
		UNRECOGNISED,
		FAILED,
	}
}
