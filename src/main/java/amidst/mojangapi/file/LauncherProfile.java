package amidst.mojangapi.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.ProfileDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.mojangapi.file.service.ClassLoaderService;

@Immutable
public class LauncherProfile {
	private final ClassLoaderService classLoaderService = new ClassLoaderService();
	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final ProfileDirectory profileDirectory;
	private final VersionDirectory versionDirectory;
	private final VersionJson versionJson;
	private final String profileName;

	public LauncherProfile(
			DotMinecraftDirectory dotMinecraftDirectory,
			ProfileDirectory profileDirectory,
			VersionDirectory versionDirectory,
			VersionJson versionJson,
			String profileName) {
		this.dotMinecraftDirectory = dotMinecraftDirectory;
		this.profileDirectory = profileDirectory;
		this.versionDirectory = versionDirectory;
		this.versionJson = versionJson;
		this.profileName = profileName;
	}

	public String getVersionId() {
		return versionJson.getId();
	}

	public String getProfileName() {
		return profileName;
	}

	public File getJar() {
		return versionDirectory.getJar();
	}

	public File getSaves() {
		return profileDirectory.getSaves();
	}

	public URLClassLoader newClassLoader() throws MalformedURLException {
		return classLoaderService.createClassLoader(
				dotMinecraftDirectory.getLibraries(),
				versionJson.getLibraries(),
				versionDirectory.getJar());
	}
}
