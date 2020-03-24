package amidst.mojangapi.file.directory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import amidst.documentation.Immutable;

@Immutable
public class DotMinecraftDirectory {
	/**
	 * Allows to customize all parts of the .minecraft directory, mainly for
	 * testing and the dev tools. Pass null to use default values.
	 */
	public static DotMinecraftDirectory newCustom(
			Path root,
			Path libraries,
			Path saves,
			Path versions,
			Path launcherProfilesJson) {
		Objects.requireNonNull(root);
		return new DotMinecraftDirectory(
				root,
				libraries != null ? libraries : root.resolve("libraries"),
				saves != null ? saves : root.resolve("saves"),
				versions != null ? versions : root.resolve("versions"),
				launcherProfilesJson != null ? launcherProfilesJson : root.resolve("launcher_profiles.json"));
	}

	private final Path root;
	private final Path libraries;
	private final Path saves;
	private final Path versions;
	private final Path launcherProfilesJson;

	public DotMinecraftDirectory(Path root) {
		this.root = root;
		this.libraries = root.resolve("libraries");
		this.saves = root.resolve("saves");
		this.versions = root.resolve("versions");
		this.launcherProfilesJson = root.resolve("launcher_profiles.json");
	}

	private DotMinecraftDirectory(Path root, Path libraries, Path saves, Path versions, Path launcherProfilesJson) {
		this.root = root;
		this.libraries = libraries;
		this.saves = saves;
		this.versions = versions;
		this.launcherProfilesJson = launcherProfilesJson;
	}

	public boolean isValid() {
		return Files.isDirectory(root)
			&& Files.isDirectory(libraries)
			&& Files.isDirectory(versions)
			&& Files.isRegularFile(launcherProfilesJson);
	}

	public ProfileDirectory asProfileDirectory() {
		return new ProfileDirectory(root);
	}

	public Path getRoot() {
		return root;
	}

	public Path getLibraries() {
		return libraries;
	}

	public Path getSaves() {
		return saves;
	}

	public Path getVersions() {
		return versions;
	}

	public Path getLauncherProfilesJson() {
		return launcherProfilesJson;
	}
}
