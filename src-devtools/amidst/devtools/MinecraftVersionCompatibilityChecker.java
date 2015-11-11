package amidst.devtools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.Util;
import amidst.clazz.Classes;
import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.translator.ClassTranslator;
import amidst.devtools.mojangapi.Version;
import amidst.devtools.mojangapi.Versions;
import amidst.devtools.settings.DevToolsSettings;
import amidst.minecraft.local.LocalMinecraftInterfaceBuilder;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class MinecraftVersionCompatibilityChecker {
	public static void main(String[] args) throws JsonSyntaxException,
			JsonIOException, IOException {
		new MinecraftVersionCompatibilityChecker(
				DevToolsSettings.INSTANCE.getMinecraftVersionsDirectory(),
				Versions.retrieve()).checkAll();
	}

	private static final List<String> OPTIONAL_CLASSES = Arrays
			.asList("BlockInit");

	private String basePath;
	private Versions versions;

	public MinecraftVersionCompatibilityChecker(String basePath,
			Versions versions) {
		this.basePath = basePath;
		this.versions = versions;
	}

	public void checkAll() {
		initCreepyStuff();
		List<Version> supported = new ArrayList<Version>();
		List<Version> unsupported = new ArrayList<Version>();
		for (Version version : versions.getVersions()) {
			if (checkOne(version)) {
				supported.add(version);
			} else {
				unsupported.add(version);
			}
		}
		displayVersionList(supported,
				"================= SUPPORTED VERSIONS =================");
		displayVersionList(unsupported,
				"================ UNSUPPORTED VERSIONS ================");
	}

	private void initCreepyStuff() {
		Util.setMinecraftDirectory();
		Util.setMinecraftLibraries();
	}

	private boolean checkOne(Version version) {
		if (version.tryDownloadClient(basePath)) {
			try {
				File jarFile = version.getLocalClientJarPath(basePath).toFile();
				ClassTranslator translator = LocalMinecraftInterfaceBuilder.StatelessResources.INSTANCE.classTranslator;
				return isSupported(Classes.countMatches(jarFile, translator));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean isSupported(
			Map<SymbolicClassDeclaration, Integer> matchesMap) {
		for (Entry<SymbolicClassDeclaration, Integer> entry : matchesMap
				.entrySet()) {
			String symbolicClassName = entry.getKey().getSymbolicClassName();
			if ((entry.getValue() == 0 && !OPTIONAL_CLASSES
					.contains(symbolicClassName)) || entry.getValue() > 1) {
				return false;
			}
		}
		return true;
	}

	private void displayVersionList(List<Version> versionList, String message) {
		System.out.println();
		System.out.println(message);
		for (Version version : versionList) {
			System.out.println(version.getId());
		}
	}
}
