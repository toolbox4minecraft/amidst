package MoF;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import amidst.Options;
import amidst.logging.Log;

public class SeedHistoryLogger {
	private static final String DEFAULT_HISTORY_FILE_NAME = "./history.txt";

	private File file = new File(getHistoryFileName());

	public void log(long seed) {
		if (!file.exists()) {
			tryCreateFile();
		}
		if (file.exists() && file.isFile()) {
			writeLine(seed);
		}
	}

	private String getHistoryFileName() {
		if (Options.instance.historyPath != null) {
			return Options.instance.historyPath;
		} else {
			return DEFAULT_HISTORY_FILE_NAME;
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

	private void writeLine(long seed) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			writer.append(createLine(seed));
		} catch (IOException e) {
			Log.w("Unable to write to history file.");
			e.printStackTrace();
		} finally {
			closeWriter(writer);
		}
	}

	private String createLine(long seed) {
		return new Timestamp(new Date().getTime()) + " " + seed + "\r\n";
	}

	private void closeWriter(FileWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			Log.w("Unable to close writer for history file.");
			e.printStackTrace();
		}
	}
}
