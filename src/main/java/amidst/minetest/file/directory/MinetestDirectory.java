package amidst.minetest.file.directory;

import java.io.File;
import java.util.Objects;

import amidst.documentation.Immutable;

@Immutable
public class MinetestDirectory {
	/**
	 * Allows to customize all parts of the minetest directory, mainly for
	 * testing and the dev tools. Pass null to use default values.
	 */
	public static MinetestDirectory newCustom(
			File root,
			File games,
			File worlds,
			File mods,
			File launcherProfilesJson) {
		Objects.requireNonNull(root);
		return new MinetestDirectory(
				root,
				games != null ? games : new File(root, "games"),
				worlds != null ? worlds : new File(root, "worlds"),
				mods != null ? mods : new File(root, "mods"),
				launcherProfilesJson != null ? launcherProfilesJson : new File(root, "launcher_profiles.json"));
	}

	private final File root;
	private final File mods;
	private final File worlds;
	private final File games;
	private final File launcherProfilesJson;

	public MinetestDirectory(File root) {
		this.root = root;
		this.mods   = new File(root, "mods");
		this.worlds = new File(root, "worlds");
		this.games  = new File(root, "games");
		this.launcherProfilesJson = new File(root, "launcher_profiles.json");
	}

	private MinetestDirectory(File root, File games, File worlds, File mods, File launcherProfilesJson) {
		this.root = root;
		this.games = games;
		this.worlds = worlds;
		this.mods = mods;
		this.launcherProfilesJson = launcherProfilesJson;
	}

	public boolean isValid() {
		return root.isDirectory() && mods.isDirectory() && games.isDirectory() && launcherProfilesJson.isFile();
	}

	public File getRoot() {
		return root;
	}

	public File getMods() {
		return mods;
	}

	public File getWorlds() {
		return worlds;
	}

	public File getGames() {
		return games;
	}

	public File getLauncherProfilesJson() {
		return launcherProfilesJson;
	}
}
