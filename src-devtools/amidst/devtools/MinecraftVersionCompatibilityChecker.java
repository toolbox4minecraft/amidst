package amidst.devtools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import amidst.Util;
import amidst.clazz.Classes;
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

	private String basePath;
	private Versions versions;

	public MinecraftVersionCompatibilityChecker(String basePath,
			Versions versions) {
		this.basePath = basePath;
		this.versions = versions;
	}

	public void checkAll() {
		initCreepyStuff();
		List<Version> successful = new ArrayList<Version>();
		List<Version> failed = new ArrayList<Version>();
		for (Version version : versions.getVersions()) {
			if (checkOne(version)) {
				successful.add(version);
			} else {
				failed.add(version);
			}
		}
		displayVersionList(successful,
				"================ SUCCESSFUL VERSIONS ============");
		displayVersionList(failed,
				"================ FAILED VERSIONS ================");
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
				return Classes.ensureExactlyOneMatches(jarFile, translator);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void displayVersionList(List<Version> versionList, String message) {
		System.out.println();
		System.out.println(message);
		for (Version version : versionList) {
			System.out.println(version.getId());
		}
	}
}
