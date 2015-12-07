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
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
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
	private static final String REMOTE_VERSION_LIST = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";
	private static final URL LOCAL_VERSION_LIST = ResourceLoader
			.getResourceURL("versions.json");

	public static VersionListJson readRemoteOrLocalVersionList() {
		return VersionListReader.readRemoteOrLocalVersionList(
				REMOTE_VERSION_LIST, LOCAL_VERSION_LIST);
	}

	public static VersionListJson remoteVersionList() throws IOException {
		return read(URIUtils.newReader(REMOTE_VERSION_LIST),
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
			result.add(profile.createProfileDirectory());
		}
		return result;
	}

	public static DotMinecraftDirectory createDotMinecraftDirectory(
			String preferedDotMinecraftDirectory,
			String preferedMinecraftLibraries) {
		if (preferedMinecraftLibraries != null) {
			return new DotMinecraftDirectory(
					DotMinecraftDirectoryFinder
							.find(preferedDotMinecraftDirectory),
					new File(preferedMinecraftLibraries));
		} else {
			return new DotMinecraftDirectory(
					DotMinecraftDirectoryFinder
							.find(preferedDotMinecraftDirectory));
		}
	}
}
