package amidst.mojangapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import amidst.mojangapi.launcherprofiles.LauncherProfiles;
import amidst.mojangapi.version.Version;
import amidst.mojangapi.versionlist.VersionList;
import amidst.utilities.URIUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public enum MojangAPI {
	;

	private static final Gson GSON = new Gson();
	private static final String REMOTE_VERSION_LIST_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";

	public static VersionList remoteVersionList() throws JsonSyntaxException,
			JsonIOException, IOException {
		return read(URIUtils.newReader(REMOTE_VERSION_LIST_URL),
				VersionList.class);
	}

	public static VersionList versionListFrom(URL url)
			throws JsonSyntaxException, JsonIOException, IOException {
		return read(URIUtils.newReader(url), VersionList.class);
	}

	public static Version versionFrom(File file) throws JsonSyntaxException,
			JsonIOException, FileNotFoundException, IOException {
		return read(URIUtils.newReader(file), Version.class);
	}

	public static LauncherProfiles launcherProfilesFrom(File file)
			throws FileNotFoundException, IOException {
		return read(URIUtils.newReader(file), LauncherProfiles.class);
	}

	private static <T> T read(Reader reader, Class<T> clazz)
			throws JsonSyntaxException, JsonIOException, IOException {
		T result = GSON.fromJson(reader, clazz);
		reader.close();
		return result;
	}
}
