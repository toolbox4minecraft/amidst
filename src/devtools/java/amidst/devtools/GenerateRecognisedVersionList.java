package amidst.devtools;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amidst.devtools.settings.DevToolsSettings;
import amidst.logging.Log;
import amidst.mojangapi.file.FilenameFactory;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.directory.DotMinecraftDirectory;
import amidst.mojangapi.file.directory.VersionDirectory;
import amidst.mojangapi.file.json.JsonReader;
import amidst.mojangapi.file.json.versionlist.VersionListEntryJson;
import amidst.mojangapi.file.json.versionlist.VersionListJson;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

public class GenerateRecognisedVersionList {
	private static final int MAX_LENGTH_MAGIC_STRING = 111;

	public static void main(String[] args) throws IOException,
			MojangApiParsingException {
		VersionListJson versionList = JsonReader.readRemoteVersionList();
		int maxVersionIdLength = 0;
		for (VersionListEntryJson version : versionList.getVersions()) {
			if (maxVersionIdLength < version.getId().length()) {
				maxVersionIdLength = version.getId().length();
			}
		}
		new GenerateRecognisedVersionList(
				DevToolsSettings.INSTANCE.getMinecraftVersionsDirectory(),
				DevToolsSettings.INSTANCE.getMinecraftLibrariesDirectory(),
				versionList, maxVersionIdLength).generate();
	}

	private final String prefix;
	private final File libraries;
	private final VersionListJson versionList;
	private final File versions;
	private final DotMinecraftDirectory dotMinecraftDirectory;
	private final List<String> knownVersions = new LinkedList<String>();
	private final List<String> knownButIncorrectVersions = new LinkedList<String>();
	private final List<String> unknownVersions = new LinkedList<String>();
	private final List<String> versionsWithError = new LinkedList<String>();
	private final List<String> magicStringCollisions = new LinkedList<String>();
	private final List<String> versionsWithoutAMatch = new LinkedList<String>();
	private final Map<String, String> magicStringToFirstMatch = new LinkedHashMap<String, String>();
	private final List<RecognisedVersion> allRecognisedVersions = new LinkedList<RecognisedVersion>();
	private final Map<String, String> output = new LinkedHashMap<String, String>();
	private final int maxVersionIdLength;

	public GenerateRecognisedVersionList(String prefix, String libraries,
			VersionListJson versionList, int maxVersionIdLength) {
		this.prefix = prefix;
		this.maxVersionIdLength = maxVersionIdLength;
		this.libraries = new File(libraries);
		this.versionList = versionList;
		this.versions = new File(prefix);
		this.dotMinecraftDirectory = new DotMinecraftDirectory(null,
				this.libraries);
		this.allRecognisedVersions.addAll(Arrays.asList(RecognisedVersion
				.values()));
	}

	public void generate() {
		for (VersionListEntryJson version : versionList.getVersions()) {
			process(version);
		}
		for (RecognisedVersion recognisedVersion : allRecognisedVersions) {
			if (!recognisedVersion.equals(RecognisedVersion.UNKNOWN)) {
				versionsWithoutAMatch.add(createEnumString(
						recognisedVersion.getName(),
						recognisedVersion.getMagicString()));
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();
		print("============== Known Versions ===============", knownVersions);
		print("======= Known But Incorrect Versions ========",
				knownButIncorrectVersions);
		print("======== Versions Without A Match ===========",
				versionsWithoutAMatch);
		print("============= Unknown Versions ==============", unknownVersions);
		print("================== Output ===================", output.values());
		print("========== Magic String Collisions ==========",
				magicStringCollisions);
		print("============ Versions With Error ============",
				versionsWithError);
		System.out.println();
		System.out
				.println("If any version are listed in the error section, this might be due to missing libraries.\n"
						+ "Start the given minecraft version with the launcher.\n"
						+ "This should download the missing libraries. Afterwards, try again.");
		System.out.println();
		System.out
				.println("When copying the magic string from this output, make sure to escape all special characters.");
		System.out.println();
		System.out
				.println("Versions without a match are probably removed from the launcher.");
		System.out.println();
		System.out
				.println("You might have to reorder the output, e.g. when a security fix for an old version came out, after the snapshots for the next version started.");
	}

	private VersionDirectory createVersionDirectory(String versionId) {
		File jar = FilenameFactory.getClientJarFile(versions, versionId);
		File json = FilenameFactory.getClientJsonFile(versions, versionId);
		return new VersionDirectory(dotMinecraftDirectory, versionId, jar, json);
	}

	private void process(VersionListEntryJson version) {
		String versionId = version.getId();
		RecognisedVersion recognisedVersion = RecognisedVersion.UNKNOWN;
		String magicString = null;
		if (version.tryDownloadClient(prefix)) {
			try {
				Log.i("version " + versionId);
				VersionDirectory versionDirectory = createVersionDirectory(versionId);
				URLClassLoader classLoader = versionDirectory
						.createClassLoader();
				magicString = RecognisedVersion
						.generateMagicString(classLoader);
				recognisedVersion = RecognisedVersion.from(magicString);
				allRecognisedVersions.remove(recognisedVersion);
				String name = recognisedVersion.getName();
				if (recognisedVersion.equals(RecognisedVersion.UNKNOWN)) {
					unknownVersions
							.add(createEnumString(versionId, magicString));
				} else if (!versionId.equals(name)) {
					knownButIncorrectVersions.add(addPadding(versionId)
							+ " is known as "
							+ createEnumString(name, magicString));
				} else {
					knownVersions.add(createEnumString(versionId, magicString));
				}
				if (magicStringToFirstMatch.containsKey(magicString)) {
					magicStringCollisions
							.add(addPadding(magicStringToFirstMatch
									.get(magicString))
									+ " and "
									+ addPadding(versionId)
									+ " with magic string '"
									+ magicString
									+ "'");
					output.put(magicString, output.get(magicString) + " "
							+ addPadding(versionId));
				} else {
					magicStringToFirstMatch.put(magicString, versionId);
					output.put(
							magicString,
							addPadding(
									createEnumString(versionId, magicString),
									MAX_LENGTH_MAGIC_STRING)
									+ " // matches the versions "
									+ addPadding(versionId));
				}
			} catch (Throwable e) {
				e.printStackTrace();
				versionsWithError.add(addPadding(versionId) + " as "
						+ addPadding(recognisedVersion.getName())
						+ " with magic string '" + magicString + "'");
			}
		} else {
			versionsWithError.add(addPadding(versionId) + " as "
					+ addPadding(recognisedVersion.getName())
					+ " with magic string '" + magicString + "'");
		}
	}

	private void print(String title, Iterable<String> lines) {
		System.out.println(title);
		for (String line : lines) {
			System.out.println(line);
		}
		System.out.println();
	}

	private String createEnumString(String versionId, String magicString) {
		return addPadding(createEnumName(versionId)) + "(\"" + magicString
				+ "\"),";
	}

	private String createEnumName(String versionId) {
		return "V" + versionId.replace(".", "_");
	}

	private String addPadding(String versionId) {
		return addPadding(versionId, maxVersionIdLength);
	}

	private String addPadding(String string, int length) {
		return string + getStringOfLength(' ', length - string.length());
	}

	private String getStringOfLength(char c, int length) {
		String result = "";
		for (int i = 0; i < length; i++) {
			result += c;
		}
		return result;
	}
}
