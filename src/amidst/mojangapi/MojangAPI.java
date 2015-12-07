package amidst.mojangapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import amidst.logging.Log;
import amidst.mojangapi.dotminecraft.DotMinecraftDirectory;
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
