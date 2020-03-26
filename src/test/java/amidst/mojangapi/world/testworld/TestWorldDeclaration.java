package amidst.mojangapi.world.testworld;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;

@Immutable
public enum TestWorldDeclaration {
	// @formatter:off
	/**
	 * Not all worlds will have data in all entries. However, we should ensure
	 * that the behavior is kept like this.
	 */
	WORLD1(RecognisedVersion._1_8_9, "amidst-test-seed", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),

	WORLD2(RecognisedVersion._1_9_pre2, "4805355321235747910", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),

    /**
     * This misses an ocean monument if the quarter-resolution map is used for the structure test.
     */
	WORLD3(RecognisedVersion._1_9_pre2, "-1364077613", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),

    /**
     * This illustrates the buggy stronghold generation mechanism of the 1.9 snapshots.
     */
	WORLD4(RecognisedVersion._15w51b, "1", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),

    /**
     * This illustrates the fixed stronghold generation mechanism of the 1.9 snapshots.
     */
	WORLD5(RecognisedVersion._1_9_pre2, "1", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),

    /**
	 * This seed contains all biomes in a 1024 radius around spawn. Source:
	 * http://www.minecraftforum.net/forums/minecraft-discussion/seeds/2500520-1-8-x-rare-seed-all-biomes-within-1024-blocks
     */
	WORLD6(RecognisedVersion._1_9_pre2, "24922", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    ),

    /**
	 * Similar to 24922
     */
	WORLD7(RecognisedVersion._1_9_pre2, "-9223372036681075344", WorldType.DEFAULT,
			TestWorldEntryNames.ALL
    );
	// @formatter:on

	private static final String RESOURCE_PREFIX = "/amidst/mojangapi/world/testworld/storage/";
	private static final String ZIP_FILE_EXTENSION = ".zip";

	private final RecognisedVersion recognisedVersion;
	private final WorldOptions worldOptions;
	private final List<String> supportedEntryNames;
	private final java.nio.file.Path directory;
	private final String directoryString;

	private TestWorldDeclaration(
			RecognisedVersion recognisedVersion,
			String seed,
			WorldType worldType,
			String... supported) {
		this.recognisedVersion = recognisedVersion;
		WorldSeed worldSeed = WorldSeed.fromUserInput(seed);
		this.worldOptions = new WorldOptions(worldSeed, worldType);
		this.supportedEntryNames = Collections.unmodifiableList(Arrays.asList(supported));
		this.directory = Paths.get(
				"src",
				"test",
				"resources",
				"amidst",
				"mojangapi",
				"world",
				"testworld",
				"storage",
				recognisedVersion.getName(),
				"" + worldSeed.getLong());
		this.directoryString = RESOURCE_PREFIX + recognisedVersion.getName() + "/" + worldSeed.getLong() + "/";
	}

	public RecognisedVersion getRecognisedVersion() {
		return recognisedVersion;
	}

	public WorldOptions getWorldOptions() {
		return worldOptions;
	}

	public boolean isSupported(String name) {
		return supportedEntryNames.contains(name);
	}

	public Iterable<String> getSupportedEntryNames() {
		return supportedEntryNames;
	}

	public void createDirectoryIfNecessary() {
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create test worlds directory", e);
		}
	}

	public Path getDirectory() {
		return directory;
	}

	public Path getZipFile(String name) {
		return directory.resolve(name + ZIP_FILE_EXTENSION);
	}

	public String getZipResourceName(String name) {
		return directoryString + name + ZIP_FILE_EXTENSION;
	}
}
