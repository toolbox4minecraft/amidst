package amidst.mojangapi.file.service;

import java.io.IOException;
import java.net.URL;

import amidst.ResourceLoader;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.parsing.FormatException;
import amidst.parsing.json.JsonReader;

@Immutable
public class VersionListService {
	private static final String REMOTE_VERSION_LIST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
	private static final URL LOCAL_VERSION_LIST = ResourceLoader
			.getResourceURL("/amidst/mojangapi/version_manifest.json");

	@NotNull
	public VersionListJson readRemoteVersionList() throws FormatException, IOException {
		return JsonReader.readLocation(REMOTE_VERSION_LIST, VersionListJson.class);
	}

	@NotNull
	public VersionListJson readLocalVersionListFromResource() throws FormatException, IOException {
		return JsonReader.readLocation(LOCAL_VERSION_LIST, VersionListJson.class);
	}
}
