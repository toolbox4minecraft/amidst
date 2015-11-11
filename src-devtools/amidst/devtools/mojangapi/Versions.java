package amidst.devtools.mojangapi;

import java.io.IOException;
import java.util.List;

import amidst.utilties.URIUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Versions {
	private static final Gson GSON = new Gson();
	private static final String VERSIONS_URL = "https://s3.amazonaws.com/Minecraft.Download/versions/versions.json";

	public static Versions retrieve() throws JsonSyntaxException,
			JsonIOException, IOException {
		return GSON.fromJson(URIUtils.newReader(VERSIONS_URL), Versions.class);
	}

	private Latest latest;
	private List<Version> versions;

	public Latest getLatest() {
		return latest;
	}

	public List<Version> getVersions() {
		return versions;
	}
}
