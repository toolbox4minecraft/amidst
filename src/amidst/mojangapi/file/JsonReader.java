package amidst.mojangapi.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import amidst.logging.Log;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.resources.ResourceLoader;
import amidst.utilities.URIUtils;

import com.google.gson.Gson;

public enum JsonReader {
	;

	private static final Gson GSON = new Gson();

	private static final String REMOTE_VERSION_LIST = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
	private static final URL LOCAL_VERSION_LIST = ResourceLoader
			.getResourceURL("versions.json");

	public static VersionListJson readRemoteOrLocalVersionList() {
		Log.i("Beginning latest version list load.");
		Log.i("Attempting to download remote version list...");
		VersionListJson remote = null;
		try {
			remote = readRemoteVersionList();
		} catch (IOException e) {
			Log.w("Unable to read remote version list.");
			Log.printTraceStack(e);
			Log.w("Aborting version list load. URL: " + REMOTE_VERSION_LIST);
		}
		if (remote != null) {
			Log.i("Successfully loaded version list. URL: "
					+ REMOTE_VERSION_LIST);
			return remote;
		}
		Log.i("Attempting to download local version list...");
		VersionListJson local = null;
		try {
			local = readLocalVersionListFromResource();
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

	public static VersionListJson readRemoteVersionList() throws IOException {
		return JsonReader.read(URIUtils.newReader(REMOTE_VERSION_LIST),
				VersionListJson.class);
	}

	public static VersionListJson readLocalVersionListFromResource()
			throws IOException {
		return JsonReader.read(URIUtils.newReader(LOCAL_VERSION_LIST),
				VersionListJson.class);
	}

	public static VersionJson readVersionFrom(File file)
			throws FileNotFoundException {
		return read(URIUtils.newReader(file), VersionJson.class);
	}

	public static LauncherProfilesJson readLauncherProfilesFrom(File file)
			throws FileNotFoundException {
		return read(URIUtils.newReader(file), LauncherProfilesJson.class);
	}

	public static <T> T read(Reader reader, Class<T> clazz) {
		T result = GSON.fromJson(reader, clazz);
		try {
			reader.close();
		} catch (IOException e) {
			Log.w("error closing reader");
			e.printStackTrace();
		}
		return result;
	}
}
