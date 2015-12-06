package amidst.mojangapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

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
}
