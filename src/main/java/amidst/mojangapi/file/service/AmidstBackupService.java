package amidst.mojangapi.file.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;

@Immutable
public class AmidstBackupService {
	public boolean tryBackupPlayersFile(SaveDirectory saveDirectory, String playerName) {
		File toDirectory = saveDirectory.getAmidst().getBackup().getPlayers();
		File from = saveDirectory.getPlayersFile(playerName);
		File to = new File(toDirectory, playerName + ".dat" + "_" + millis());
		return tryBackup(toDirectory, from, to);
	}

	public boolean tryBackupPlayerdataFile(SaveDirectory saveDirectory, String playerUUID) {
		File toDirectory = saveDirectory.getAmidst().getBackup().getPlayerdata();
		File from = saveDirectory.getPlayerdataFile(playerUUID);
		File to = new File(toDirectory, playerUUID + ".dat" + "_" + millis());
		return tryBackup(toDirectory, from, to);
	}

	public boolean tryBackupLevelDat(SaveDirectory saveDirectory) {
		File toDirectory = saveDirectory.getAmidst().getBackup().getRoot();
		File from = saveDirectory.getLevelDat();
		File to = new File(toDirectory, "level.dat" + "_" + millis());
		return tryBackup(toDirectory, from, to);
	}

	private String millis() {
		return new Timestamp(System.currentTimeMillis())
				.toString()
				.replace(" ", "_")
				.replace(":", "-")
				.replace(".", "_");
	}

	private boolean tryBackup(File toDirectory, File from, File to) {
		return ensureDirectoryExists(toDirectory) && tryCopy(from, to) && to.isFile();
	}

	private boolean ensureDirectoryExists(File directory) {
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return directory.isDirectory();
	}

	private boolean tryCopy(File from, File to) {
		try {
			Files.copy(from.toPath(), to.toPath());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
