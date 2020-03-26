package amidst.mojangapi.world;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@ThreadSafe
public class SeedHistoryLogger {
	public static SeedHistoryLogger from(Path seedHistoryFile) {
		if (seedHistoryFile != null) {
			AmidstLogger.info("using seed history file: '" + seedHistoryFile + "'");
			return new SeedHistoryLogger(seedHistoryFile, true, true);
		} else {
			return new SeedHistoryLogger(Paths.get(HISTORY_TXT), false, true);
		}
	}

	public static SeedHistoryLogger createDisabled() {
		return new SeedHistoryLogger(Paths.get(HISTORY_TXT), false, false);
	}

	private static final String HISTORY_TXT = "history.txt";

	private final Path file;
	private final boolean createIfNecessary;
	private final boolean isEnabled;

	public SeedHistoryLogger(@NotNull Path file, boolean createIfNecessary, boolean isEnabled) {
		Objects.requireNonNull(file);
		this.file = file;
		this.createIfNecessary = createIfNecessary;
		this.isEnabled = isEnabled;
	}

	public void log(RecognisedVersion recognisedVersion, WorldSeed worldSeed) {
		if (isEnabled) {
			doLog(recognisedVersion, worldSeed);
		}
	}

	private synchronized void doLog(RecognisedVersion recognisedVersion, WorldSeed worldSeed) {
		if (createIfNecessary && !Files.exists(file)) {
			tryCreateFile();
		}
		if (Files.isRegularFile(file)) {
			writeLine(createLine(recognisedVersion, worldSeed));
		} else {
			AmidstLogger.info("Not writing to seed history file, because it does not exist: {}", file);
		}
	}

	private String createLine(RecognisedVersion recognisedVersion, WorldSeed worldSeed) {
		String recognisedVersionName = recognisedVersion.getName();
		String timestamp = createTimestamp();
		String seedString = getSeedString(worldSeed);
		return recognisedVersionName + ", " + timestamp + ", " + seedString;
	}

	private String createTimestamp() {
		return new Timestamp(new Date().getTime()).toString();
	}

	private String getSeedString(WorldSeed worldSeed) {
		String text = worldSeed.getText();
		if (text != null) {
			return worldSeed.getLong() + ", " + text;
		} else {
			return worldSeed.getLong() + "";
		}
	}

	private void tryCreateFile() {
		try {
			Files.createFile(file);
		} catch (IOException e) {
			AmidstLogger.warn(e, "Unable to create seed history file: {}", file);
		}
	}

	private void writeLine(String line) {
		try (BufferedWriter writer = Files.newBufferedWriter(file,
				StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
			writer.write(line);
			writer.write('\n');
		} catch (IOException e) {
			AmidstLogger.warn(e, "Unable to write to seed history file: {}", file);
		}
	}
}
