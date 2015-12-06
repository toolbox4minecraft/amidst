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
			tryAddSaveDirectory(result, save);
		}
		return result;
	}

	private static void tryAddSaveDirectory(List<SaveDirectory> result,
			File save) {
		try {
			result.add(createSaveDirectory(save));
		} catch (FileNotFoundException e) {
			Log.w("Unable to load world save.");
			e.printStackTrace();
		}
	}

	public static SaveDirectory createSaveDirectory(File save)
			throws FileNotFoundException {
		File players = new File(save, "players");
		File playerdata = new File(save, "playerdata");
		File levelDat = new File(save, "level.dat");
		ensureFileExists("Unable to load world save", save);
		ensureAnyFileExists("Unable to load world save", players, playerdata);
		ensureFileExists("Unable to load world save", levelDat);
		return new SaveDirectory(save, players, playerdata, levelDat);
	}

	public static Map<String, VersionDirectory> createVersionDirectories(
			File versions) {
		Map<String, VersionDirectory> result = new HashMap<String, VersionDirectory>();
		for (File file : versions.listFiles()) {
			tryAddVersionDirectory(result, versions, file.getName());
		}
		return result;
	}

	private static void tryAddVersionDirectory(
			Map<String, VersionDirectory> result, File versions, String id) {
		File jar = FilenameFactory.getClientJarFile(versions, id);
		File json = FilenameFactory.getClientJsonFile(versions, id);
		try {
			result.put(id, createVersionDirectory(jar, json));
		} catch (Exception e) {
			Log.w("Unable to load version directory.");
			e.printStackTrace();
		}
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
			tryAddProfileDirectory(result, profile);
		}
		return result;
	}

	private static void tryAddProfileDirectory(
			ArrayList<ProfileDirectory> result, LaucherProfileJson profile) {
		try {
			result.add(createProfileDirectory(profile));
		} catch (Exception e) {
			Log.w("Unable to load profile directory.");
			e.printStackTrace();
		}
	}

	public static ProfileDirectory createProfileDirectory(
			LaucherProfileJson profileJson) throws FileNotFoundException {
		File profile = new File(profileJson.getGameDir());
		File saves = new File(profile, "saves");
		ensureFileExists("Unable to load profile directory", profile);
		ensureFileExists("Unable to load profile directory", saves);
		return new ProfileDirectory(profile, saves, profileJson);
	}

	private static void ensureFileExists(String message, File file)
			throws FileNotFoundException {
		if (!file.isFile()) {
			throw new FileNotFoundException(message + ": " + file
					+ " is not a file");
		}
	}

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
