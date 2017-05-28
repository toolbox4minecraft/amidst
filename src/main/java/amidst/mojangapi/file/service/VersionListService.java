package amidst.mojangapi.file.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import amidst.ResourceLoader;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

@Immutable
public class VersionListService {
	private static final String REMOTE_VERSION_LIST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
	private static final URL LOCAL_VERSION_LIST = ResourceLoader
			.getResourceURL("/amidst/mojangapi/version_manifest.json");

	@NotNull
	public VersionListJson readRemoteOrLocalVersionList() throws FileNotFoundException {
		AmidstLogger.info("Beginning latest version list load.");
		AmidstLogger.info("Attempting to download remote version list...");
		try {
			VersionListJson remote = readRemoteVersionList();
			AmidstLogger.info("Successfully loaded version list. URL: " + REMOTE_VERSION_LIST);
			return remote;
		} catch (IOException | MojangApiParsingException e) {
			AmidstLogger.warn("Unable to read remote version list.");
			AmidstLogger.warn(e);
			AmidstLogger.warn("Aborting version list load. URL: " + REMOTE_VERSION_LIST);
		}
		AmidstLogger.info("Attempting to load local version list...");
		try {
			VersionListJson local = readLocalVersionListFromResource();
			AmidstLogger.info("Successfully loaded version list. URL: " + LOCAL_VERSION_LIST);
			return local;
		} catch (IOException | MojangApiParsingException e) {
			AmidstLogger.warn("Unable to read local version list.");
			AmidstLogger.warn(e);
			AmidstLogger.warn("Aborting version list load. URL: " + LOCAL_VERSION_LIST);
		}
		AmidstLogger.warn("Failed to load both remote and local version list.");
		throw new FileNotFoundException("unable to read version list");
	}

	@NotNull
	public VersionListJson readRemoteVersionList() throws MojangApiParsingException, IOException {
		return JsonReader.readLocation(REMOTE_VERSION_LIST, VersionListJson.class);
	}

	@NotNull
	public VersionListJson readLocalVersionListFromResource() throws MojangApiParsingException, IOException {
		return JsonReader.readLocation(LOCAL_VERSION_LIST, VersionListJson.class);
	}
}
