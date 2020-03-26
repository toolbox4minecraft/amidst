package amidst.mojangapi.file.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;

@Immutable
public class AmidstBackupService {
	public boolean tryBackupPlayersFile(SaveDirectory saveDirectory, String playerName) {
		Path toDirectory = saveDirectory.getAmidst().getBackup().getPlayers();
		Path from = saveDirectory.getPlayersFile(playerName);
		Path to = toDirectory.resolve(playerName + ".dat" + "_" + millis());
		return tryBackup(toDirectory, from, to);
	}

	public boolean tryBackupPlayerdataFile(SaveDirectory saveDirectory, String playerUUID) {
		Path toDirectory = saveDirectory.getAmidst().getBackup().getPlayerdata();
		Path from = saveDirectory.getPlayerdataFile(playerUUID);
		Path to = toDirectory.resolve(playerUUID + ".dat" + "_" + millis());
		return tryBackup(toDirectory, from, to);
	}

	public boolean tryBackupLevelDat(SaveDirectory saveDirectory) {
		Path toDirectory = saveDirectory.getAmidst().getBackup().getRoot();
		Path from = saveDirectory.getLevelDat();
		Path to = toDirectory.resolve("level.dat" + "_" + millis());
		return tryBackup(toDirectory, from, to);
	}

	private String millis() {
		return new Timestamp(System.currentTimeMillis())
				.toString()
				.replace(" ", "_")
				.replace(":", "-")
				.replace(".", "_");
	}

	private boolean tryBackup(Path toDirectory, Path from, Path to) {
		return ensureDirectoryExists(toDirectory) && tryCopy(from, to) && Files.isRegularFile(to);
	}

	private boolean ensureDirectoryExists(Path directory) {
		try {
			Files.createDirectories(directory);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean tryCopy(Path from, Path to) {
		try {
			Files.copy(from, to);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
