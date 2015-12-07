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
import amidst.mojangapi.launcherprofiles.LauncherProfileJson;
import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.version.VersionJson;
import amidst.mojangapi.versionlist.VersionListJson;
import amidst.resources.ResourceLoader;
import amidst.utilities.URIUtils;

import com.google.gson.Gson;

public enum MojangAPI {
	;

	private static final Gson GSON = new Gson();
	private static final String REMOTE_VERSION_LIST_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
	private static final URL LOCAL_VERSION_LIST = ResourceLoader
			.getResourceURL("versions.json");

	// TODO: move somewhere else and in worker
	@Deprecated
	public static VersionListJson readRemoteOrLocalVersionList() {
		Log.i("Beginning latest version list load.");
		Log.i("Attempting to download remote version list...");
		VersionListJson remote = null;
		try {
			remote = remoteVersionList();
		} catch (IOException e) {
			Log.w("Unable to read remote version list.");
			Log.printTraceStack(e);
			Log.w("Aborting version list load. URL: " + REMOTE_VERSION_LIST_URL);
		}
		if (remote != null) {
			Log.i("Successfully loaded version list. URL: "
					+ REMOTE_VERSION_LIST_URL);
			return remote;
		}
		Log.i("Attempting to download local version list...");
		VersionListJson local = null;
		try {
			local = localVersionListFromResource();
		} catch (IOException e) {
			Log.w("Unable to read local version list.");
			Log.printTraceStack(e);
			Log.w("Aborting version list load. URL: " + LOCAL_VERSION_LIST);
		}
		if (local != null) {
			Log.i("Successfully loaded version list. URL: "
					+ LOCAL_VERSION_LIST);
			return local;
		}
		Log.w("Failed to load both remote and local version list.");
		return null;
	}

	public static VersionListJson remoteVersionList() throws IOException {
		return read(URIUtils.newReader(REMOTE_VERSION_LIST_URL),
				VersionListJson.class);
	}

	public static VersionListJson localVersionListFromResource()
			throws IOException {
		return read(URIUtils.newReader(LOCAL_VERSION_LIST),
				VersionListJson.class);
	}

	public static VersionJson versionFrom(File file)
			throws FileNotFoundException {
		return read(URIUtils.newReader(file), VersionJson.class);
	}

	public static LauncherProfilesJson launcherProfilesFrom(File file)
			throws FileNotFoundException {
		return read(URIUtils.newReader(file), LauncherProfilesJson.class);
	}

	private static <T> T read(Reader reader, Class<T> clazz) {
		T result = GSON.fromJson(reader, clazz);
		try {
			reader.close();
		} catch (IOException e) {
			Log.w("error closing reader");
			e.printStackTrace();
		}
		return result;
	}

	public static List<SaveDirectory> createSaveDirectories(File saves) {
		List<SaveDirectory> result = new ArrayList<SaveDirectory>();
		for (File save : saves.listFiles()) {
			result.add(new SaveDirectory(save));
		}
		return result;
	}

	public static Map<String, VersionDirectory> createVersionDirectories(
			File versions) {
		Map<String, VersionDirectory> result = new HashMap<String, VersionDirectory>();
		for (File version : versions.listFiles()) {
			String id = version.getName();
			result.put(id, new VersionDirectory(versions, id));
		}
		return result;
	}

	public static List<ProfileDirectory> createProfileDirectories(
			LauncherProfilesJson launcherProfilesJson) {
		List<ProfileDirectory> result = new ArrayList<ProfileDirectory>();
		for (LauncherProfileJson profile : launcherProfilesJson.getProfiles()) {
			result.add(new ProfileDirectory(new File(profile.getGameDir())));
		}
		return result;
	}
}
