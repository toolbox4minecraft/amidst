package amidst.mojangapi.world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import amidst.documentation.NotNull;
import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@ThreadSafe
public class SeedHistoryLogger {
	public static SeedHistoryLogger from(String filename) {
		if (filename != null) {
			Log.i("using seed history file: '" + filename + "'");
			return new SeedHistoryLogger(new File(filename), true);
		} else {
			return new SeedHistoryLogger(new File("history.txt"), false);
		}
	}

	private final File file;
	private final boolean createIfNecessary;

	public SeedHistoryLogger(@NotNull File file, boolean createIfNecessary) {
		Objects.requireNonNull(file);
		this.file = file;
		this.createIfNecessary = createIfNecessary;
	}

	public synchronized void log(RecognisedVersion recognisedVersion, WorldSeed worldSeed) {
		if (createIfNecessary && !file.exists()) {
			tryCreateFile();
		}
		if (file.isFile()) {
			writeLine(createLine(recognisedVersion, worldSeed));
		} else {
			Log.i("Not writing to seed history file, because it does not exist: " + file);
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
			file.createNewFile();
		} catch (IOException e) {
			Log.w("Unable to create seed history file: " + file);
			e.printStackTrace();
		}
	}

	private void writeLine(String line) {
		try (PrintStream stream = new PrintStream(new FileOutputStream(file, true))) {
			stream.println(line);
		} catch (IOException e) {
			Log.w("Unable to write to seed history file: " + file);
			e.printStackTrace();
		}
	}
}
