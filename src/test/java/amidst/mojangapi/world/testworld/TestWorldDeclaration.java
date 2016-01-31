package amidst.mojangapi.world.testworld;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

@Immutable
public enum TestWorldDeclaration {
	/**
	 * Not all worlds will have data in all entries. However, we should ensure
	 * that the behavior is kept like this.
	 */
	// @formatter:off
	WORLD1(RecognisedVersion.V1_8_9, "amidst-test-seed", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),
    
	WORLD2(RecognisedVersion.V16w02a, "4805355321235747910", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),
    
    /**
     * This misses an ocean monument if the quarter-resolution map is used for the structure test.
     */
	WORLD3(RecognisedVersion.V16w02a, "-1364077613", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),
    
    /**
     * This illustrates the probably buggy stronghold generation mechanism of the 1.9 snapshots.
     */
	WORLD4(RecognisedVersion.V16w02a, "1", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),
    
    /**
	 * This seed contains all biomes in a 1024 radius around spawn. Source:
	 * http://www.minecraftforum.net/forums/minecraft-discussion/seeds/2500520-1-8-x-rare-seed-all-biomes-within-1024-blocks
     */
	WORLD5(RecognisedVersion.V16w02a, "24922", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),
    
    /**
	 * Similar to 24922
     */
	WORLD6(RecognisedVersion.V16w02a, "-9223372036681075344", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    );
	// @formatter:on

	private static final String RESOURCE_PREFIX = "/amidst/mojangapi/world/testworld/storage/";
	private static final String ZIP_FILE_EXTENSION = ".zip";

	private final RecognisedVersion recognisedVersion;
	private final WorldSeed worldSeed;
	private final WorldType worldType;
	private final List<String> supportedEntryNames;
	private final File directory;
	private final String directoryString;

	private TestWorldDeclaration(RecognisedVersion recognisedVersion,
			String seed, WorldType worldType, String... supported) {
		this.recognisedVersion = recognisedVersion;
		this.worldSeed = WorldSeed.fromUserInput(seed);
		this.worldType = worldType;
		this.supportedEntryNames = Collections.unmodifiableList(Arrays
				.asList(supported));
		this.directory = Paths.get("src", "test", "resources", "amidst",
				"mojangapi", "world", "testworld", "storage",
				recognisedVersion.getName(), "" + worldSeed.getLong()).toFile();
		this.directoryString = RESOURCE_PREFIX + recognisedVersion.getName()
				+ "/" + worldSeed.getLong() + "/";
	}

	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	public WorldSeed getWorldSeed() {
		return worldSeed;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public boolean isSupported(String name) {
		return supportedEntryNames.contains(name);
	}

	public Iterable<String> getSupportedEntryNames() {
		return supportedEntryNames;
	}

	public void createDirectoryIfNecessary() {
		directory.mkdirs();
	}

	public File getDirectory() {
		return directory;
	}

	public File getZipFile(String name) {
		return new File(directory, name + ZIP_FILE_EXTENSION);
	}

	public String getZipResourceName(String name) {
		return directoryString + name + ZIP_FILE_EXTENSION;
	}
}
