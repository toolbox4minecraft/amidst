package amidst.mojangapi.file.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import amidst.ResourceLoader;
import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.URIUtils;
import amidst.mojangapi.file.json.launcherprofiles.LauncherProfilesJson;
import amidst.mojangapi.file.json.player.PlayerJson;
import amidst.mojangapi.file.json.player.SimplePlayerJson;
import amidst.mojangapi.file.json.version.VersionJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;

/**
 * This is a utility class used to read JSON data. Please use this class only to
 * read JSON data provided by Mojang, because it throws a
 * MojangApiParsingException when an error occurs.
 */
@Immutable
public enum JsonReader {
	;

	private static final Gson GSON = new Gson();

	private static final String REMOTE_VERSION_LIST = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
	private static final URL LOCAL_VERSION_LIST = ResourceLoader
			.getResourceURL("/amidst/mojangapi/version_manifest.json");

	private static final String UUID_TO_PROFILE = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private static final String PLAYERNAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/";

	@NotNull
	public static VersionListJson readRemoteOrLocalVersionList() throws FileNotFoundException {
		AmidstLogger.info("Beginning latest version list load.");
		AmidstLogger.info("Attempting to download remote version list...");
		VersionListJson remote = null;
		try {
			remote = readRemoteVersionList();
		} catch (IOException | MojangApiParsingException e) {
			AmidstLogger.warn("Unable to read remote version list.");
			AmidstLogger.warn(e);
			AmidstLogger.warn("Aborting version list load. URL: " + REMOTE_VERSION_LIST);
		}
		if (remote != null) {
			AmidstLogger.info("Successfully loaded version list. URL: " + REMOTE_VERSION_LIST);
			return remote;
		}
		AmidstLogger.info("Attempting to load local version list...");
		VersionListJson local = null;
		try {
			local = readLocalVersionListFromResource();
		} catch (IOException | MojangApiParsingException e) {
			AmidstLogger.warn("Unable to read local version list.");
			AmidstLogger.warn(e);
			AmidstLogger.warn("Aborting version list load. URL: " + LOCAL_VERSION_LIST);
		}
		if (local != null) {
			AmidstLogger.info("Successfully loaded version list. URL: " + LOCAL_VERSION_LIST);
			return local;
		}
		AmidstLogger.warn("Failed to load both remote and local version list.");
		throw new FileNotFoundException("unable to read version list");
	}

	@NotNull
	public static VersionListJson readRemoteVersionList() throws IOException, MojangApiParsingException {
		return read(URIUtils.newReader(REMOTE_VERSION_LIST), VersionListJson.class);
	}

	@NotNull
	public static VersionListJson readLocalVersionListFromResource() throws IOException, MojangApiParsingException {
		return read(URIUtils.newReader(LOCAL_VERSION_LIST), VersionListJson.class);
	}

	@NotNull
	public static VersionJson readVersionFrom(File file) throws MojangApiParsingException, IOException {
		return read(URIUtils.newReader(file), VersionJson.class);
	}

	@NotNull
	public static LauncherProfilesJson readLauncherProfilesFrom(File file)
			throws MojangApiParsingException,
			IOException {
		return read(URIUtils.newReader(file), LauncherProfilesJson.class);
	}

	@NotNull
	public static <T> T read(Reader reader, Class<T> clazz) throws MojangApiParsingException, IOException {
		try (Reader theReader = reader) {
			T result = GSON.fromJson(theReader, clazz);
			if (result != null) {
				return result;
			} else {
				throw new MojangApiParsingException("result was null");
			}
		} catch (JsonSyntaxException e) {
			throw new MojangApiParsingException(e);
		} catch (JsonIOException e) {
			throw new IOException(e);
		}
	}

	@NotNull
	public static PlayerJson readPlayerFromUUID(String uuid) throws IOException, MojangApiParsingException {
		return read(URIUtils.newReader(UUID_TO_PROFILE + uuid), PlayerJson.class);
	}

	@NotNull
	public static SimplePlayerJson readSimplePlayerFromPlayerName(String playerName)
			throws IOException,
			MojangApiParsingException {
		return read(URIUtils.newReader(PLAYERNAME_TO_UUID + playerName), SimplePlayerJson.class);
	}

	@NotNull
	public static <T> T read(String string, Class<T> clazz) throws MojangApiParsingException {
		try {
			T result = GSON.fromJson(string, clazz);
			if (result != null) {
				return result;
			} else {
				throw new MojangApiParsingException("result was null");
			}
		} catch (JsonSyntaxException e) {
			throw new MojangApiParsingException(e);
		}
	}
}
