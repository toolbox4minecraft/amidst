package amidst.mojangapi.world;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;

@ThreadSafe
public class SeedHistoryLogger {
	private final File file;

	public SeedHistoryLogger(String filename) {
		this.file = getHistoryFile(filename);
	}

	private File getHistoryFile(String filename) {
		if (filename != null) {
			Log.i("using seed history file: '" + filename + "'");
			return new File(filename);
		} else {
			return null;
		}
	}

	public synchronized void log(WorldSeed seed) {
		if (file != null) {
			if (!file.exists()) {
				tryCreateFile();
			}
			if (file.exists() && file.isFile()) {
				writeLine(seed);
			} else {
				Log.w("unable to write seed to seed log file");
			}
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
		try (FileWriter writer = new FileWriter(file, true)) {
			writer.append(createLine(seed));
		} catch (IOException e) {
			Log.w("Unable to write to history file.");
			e.printStackTrace();
		}
	}

	private String createLine(WorldSeed seed) {
		return new Timestamp(new Date().getTime()) + " " + seed.getLong()
				+ "\r\n";
	}
}
