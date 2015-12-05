package amidst.mojangapi.versionlist;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import amidst.utilities.URIUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class VersionList {
	private static final Gson GSON = new Gson();
	private static final String REMOTE_VERSIONS_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";

	public static VersionList retrieveRemote() throws JsonSyntaxException,
			JsonIOException, IOException {
		return doRetrieve(URIUtils.newReader(REMOTE_VERSIONS_URL));
	}

	public static VersionList retrieve(URL url) throws JsonSyntaxException,
			JsonIOException, IOException {
		return doRetrieve(URIUtils.newReader(url));
	}

	private static VersionList doRetrieve(BufferedReader reader)
			throws JsonSyntaxException, JsonIOException {
		return GSON.fromJson(reader, VersionList.class);
	}

	private List<VersionListEntry> versions;

	public VersionList() {
		// no-argument constructor for gson
	}

	public List<VersionListEntry> getVersions() {
		return versions;
	}
}
