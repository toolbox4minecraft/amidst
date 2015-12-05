package amidst.mojangapi.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import amidst.utilities.URIUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class VersionDeclarations {
	private static final Gson GSON = new Gson();
	private static final String REMOTE_VERSIONS_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";

	public static VersionDeclarations retrieveRemote()
			throws JsonSyntaxException, JsonIOException, IOException {
		return doRetrieve(URIUtils.newReader(REMOTE_VERSIONS_URL));
	}

	public static VersionDeclarations retrieve(URL url)
			throws JsonSyntaxException, JsonIOException, IOException {
		return doRetrieve(URIUtils.newReader(url));
	}

	private static VersionDeclarations doRetrieve(BufferedReader reader) {
		return GSON.fromJson(reader, VersionDeclarations.class);
	}

	private LatestVersionDeclaration latest;
	private List<VersionDeclaration> versions;

	public VersionDeclarations() {
		// no-argument constructor for gson
	}

	public LatestVersionDeclaration getLatest() {
		return latest;
	}

	public List<VersionDeclaration> getVersions() {
		return versions;
	}
}
