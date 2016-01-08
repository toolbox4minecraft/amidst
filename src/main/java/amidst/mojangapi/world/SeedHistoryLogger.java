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

	public synchronized void log(WorldSeed seed) {
		if (createIfNecessary && !file.exists()) {
			tryCreateFile();
		}
		if (file.isFile()) {
			writeLine(seed);
		} else {
			Log.w("unable to write seed to seed history log file");
		}
	}

	private void tryCreateFile() {
		try {
			file.createNewFile();
		} catch (IOException e) {
			Log.w("Unable to create history file: " + file);
			e.printStackTrace();
		}
	}

	private void writeLine(WorldSeed seed) {
		try (PrintStream stream = new PrintStream(new FileOutputStream(file,
				true))) {
			stream.println(createLine(seed));
		} catch (IOException e) {
			Log.w("Unable to write to history file.");
			e.printStackTrace();
		}
	}

	private String createLine(WorldSeed seed) {
		String text = seed.getText();
		if (text != null) {
			return new Timestamp(new Date().getTime()) + " " + seed.getLong()
					+ " " + text;
		} else {
			return new Timestamp(new Date().getTime()) + " " + seed.getLong();
		}
	}
}
