package amidst.devtools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.Classes;
import amidst.clazz.real.JarFileParsingException;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.translator.ClassTranslator;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.minecraftinterface.local.DefaultClassTranslator;

/**
 * This only checks if there is exactly one class in the jar file for each
 * class, defined by the class translator. Optional classes can be missing.
 * However, it does not check if the class has all the required constructors,
 * methods and fields. It also does not try to use the found classes.
 */
public class MinecraftVersionCompatibilityChecker {
	private String prefix;
	private VersionList versionList;

	public MinecraftVersionCompatibilityChecker(String prefix, VersionList versionList) {
		this.prefix = prefix;
		this.versionList = versionList;
	}

	public void run() {
		List<Version> supported = new ArrayList<>();
		List<Version> unsupported = new ArrayList<>();
		for (Version version : versionList.getVersions()) {
			if (checkOne(version)) {
				supported.add(version);
			} else {
				unsupported.add(version);
			}
		}
		displayVersionList(supported, "================= SUPPORTED VERSIONS =================");
		displayVersionList(unsupported, "================ UNSUPPORTED VERSIONS ================");
	}

	private boolean checkOne(Version version) {
		if (version.tryDownloadClient(prefix)) {
			try {
				File jarFile = version.getClientJarFile(new File(prefix));
				ClassTranslator translator = DefaultClassTranslator.INSTANCE.get();
				return isSupported(Classes.countMatches(jarFile, translator));
			} catch (FileNotFoundException | JarFileParsingException e) {
				e.printStackTrace();
			}
		}
		return false;
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

	private void displayVersionList(List<Version> versions, String message) {
		System.out.println();
		System.out.println(message);
		for (Version version : versions) {
			System.out.println(version.getId());
		}
	}
}
