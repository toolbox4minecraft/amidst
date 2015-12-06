package amidst.mojangapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amidst.logging.Log;
import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.SaveDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;
import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.version.VersionJson;
import amidst.mojangapi.versionlist.VersionListJson;
import amidst.utilities.URIUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public enum MojangAPI {
	;

	private static final Gson GSON = new Gson();
	private static final String REMOTE_VERSION_LIST_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";

	public static VersionListJson remoteVersionList()
			throws JsonSyntaxException, JsonIOException, IOException {
		return read(URIUtils.newReader(REMOTE_VERSION_LIST_URL),
				VersionListJson.class);
	}

	public static VersionListJson versionListFrom(URL url)
			throws JsonSyntaxException, JsonIOException, IOException {
		return read(URIUtils.newReader(url), VersionListJson.class);
	}

	public static VersionJson versionFrom(File file)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException,
			IOException {
		return read(URIUtils.newReader(file), VersionJson.class);
	}

	public static LauncherProfilesJson launcherProfilesFrom(File file)
			throws FileNotFoundException, IOException {
		return read(URIUtils.newReader(file), LauncherProfilesJson.class);
	}

	private static <T> T read(Reader reader, Class<T> clazz)
			throws JsonSyntaxException, JsonIOException, IOException {
		T result = GSON.fromJson(reader, clazz);
		reader.close();
		return result;
	}

	public static List<SaveDirectory> createSaveDirectories(File saves) {
		List<SaveDirectory> result = new ArrayList<SaveDirectory>();
		for (File save : saves.listFiles()) {
			SaveDirectory saveDirectory = tryCreateSaveDirectory(result, save);
			if (saveDirectory != null) {
				result.add(saveDirectory);
			}
		}
		return result;
	}

	public static SaveDirectory tryCreateSaveDirectory(
			List<SaveDirectory> result, File save) {
		try {
			return createSaveDirectory(save);
		} catch (FileNotFoundException e) {
			Log.w("Unable to load world save.");
			e.printStackTrace();
		}
		return null;
	}

	public static SaveDirectory createSaveDirectory(File save)
			throws FileNotFoundException {
		File players = new File(save, "players");
		File playerdata = new File(save, "playerdata");
		File levelDat = new File(save, "level.dat");
		ensureDirectoryExists("Unable to load world save", save);
		ensureAnyDirectoryExists("Unable to load world save", players,
				playerdata);
		ensureFileExists("Unable to load world save", levelDat);
		return new SaveDirectory(save, players, playerdata, levelDat);
	}

	public static Map<String, VersionDirectory> createVersionDirectories(
			File versions) {
		Map<String, VersionDirectory> result = new HashMap<String, VersionDirectory>();
		for (File file : versions.listFiles()) {
			String id = file.getName();
			VersionDirectory versionDirectory = tryCreateVersionDirectory(
					versions, id);
			if (versionDirectory != null) {
				result.put(id, versionDirectory);
			}
		}
		return result;
	}

	public static VersionDirectory tryCreateVersionDirectory(File versions,
			String id) {
		File jar = FilenameFactory.getClientJarFile(versions, id);
		File json = FilenameFactory.getClientJsonFile(versions, id);
		try {
			return createVersionDirectory(jar, json);
		} catch (Exception e) {
			Log.w("Unable to load version directory.");
			e.printStackTrace();
		}
		return null;
	}

	public static VersionDirectory createVersionDirectory(File jar, File json)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException,
			IOException {
		ensureFileExists("Unable to load version directory", jar);
		ensureFileExists("Unable to load version directory", json);
		return new VersionDirectory(jar, MojangAPI.versionFrom(json));
	}

	public static List<ProfileDirectory> createProfileDirectories(
			LauncherProfilesJson launcherProfilesJson) {
		ArrayList<ProfileDirectory> result = new ArrayList<ProfileDirectory>();
		for (LaucherProfileJson profile : launcherProfilesJson.getProfiles()) {
			ProfileDirectory profileDirectory = tryCreateProfileDirectory(profile);
			if (profileDirectory != null) {
				result.add(profileDirectory);
			}
		}
		return result;
	}

	public static ProfileDirectory tryCreateProfileDirectory(
			LaucherProfileJson profile) {
		try {
			return createProfileDirectory(profile);
		} catch (FileNotFoundException e) {
			Log.w("Unable to load profile directory.");
			e.printStackTrace();
		}
		return null;
	}

	public static ProfileDirectory createProfileDirectory(
			LaucherProfileJson profileJson) throws FileNotFoundException {
		File profile = new File(profileJson.getGameDir());
		File saves = new File(profile, "saves");
		ensureDirectoryExists("Unable to load profile directory", profile);
		ensureDirectoryExists("Unable to load profile directory", saves);
		return new ProfileDirectory(profile, saves, profileJson);
	}

	private static void ensureDirectoryExists(String message, File directory)
			throws FileNotFoundException {
		if (!directory.isDirectory()) {
			throw new FileNotFoundException(message + ": " + directory
					+ " is not a directory");
		}
	}

	private static void ensureAnyDirectoryExists(String message,
			File... directories) throws FileNotFoundException {
		for (File directory : directories) {
			if (directory.isDirectory()) {
				return;
			}
		}
		throw new FileNotFoundException(message);
	}

	private static void ensureFileExists(String message, File file)
			throws FileNotFoundException {
		if (!file.isFile()) {
			throw new FileNotFoundException(message + ": " + file
					+ " is not a file");
		}
	}

	@SuppressWarnings("unused")
	private static void ensureAnyFileExists(String message, File... files)
			throws FileNotFoundException {
		for (File file : files) {
			if (file.isFile()) {
				return;
			}
		}
		throw new FileNotFoundException(message);
	}
}
