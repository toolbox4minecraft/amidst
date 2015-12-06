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

import amidst.mojangapi.dotminecraft.ProfileDirectory;
import amidst.mojangapi.dotminecraft.SaveDirectory;
import amidst.mojangapi.dotminecraft.VersionDirectory;
import amidst.mojangapi.launcherprofiles.LaucherProfileJson;
import amidst.mojangapi.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.version.VersionJson;
import amidst.mojangapi.versionlist.VersionListJson;
import amidst.resources.ResourceLoader;
import amidst.utilities.URIUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public enum MojangAPI {
	;

	private static final Gson GSON = new Gson();
	private static final String REMOTE_VERSION_LIST_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
	private static final URL LOCAL_VERSION_LIST = ResourceLoader
			.getResourceURL("versions.json");

	public static VersionListJson remoteVersionList()
			throws JsonSyntaxException, JsonIOException, IOException {
		return read(URIUtils.newReader(REMOTE_VERSION_LIST_URL),
				VersionListJson.class);
	}

	public static VersionListJson localVersionListFromResource()
			throws JsonSyntaxException, JsonIOException, IOException {
		return read(URIUtils.newReader(LOCAL_VERSION_LIST),
				VersionListJson.class);
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
		for (LaucherProfileJson profile : launcherProfilesJson.getProfiles()) {
			result.add(new ProfileDirectory(new File(profile.getGameDir())));
		}
		return result;
	}
}
