package amidst.mojangapi.file.directory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;

public class SaveAmidstBackupDirectory {
	private final File root;
	private final File players;
	private final File playerdata;

	public SaveAmidstBackupDirectory(File root) {
		this.root = root;
		this.players = new File(root, "players");
		this.playerdata = new File(root, "playerdata");
	}

	public File getRoot() {
		return root;
	}

	public File getPlayers() {
		return players;
	}

	public File getPlayerdata() {
		return playerdata;
	}

	public File getPlayersFile(String playerName) {
		return new File(players, playerName + ".dat" + "_" + millis());
	}

	public File getPlayerdataFile(String playerUUID) {
		return new File(playerdata, playerUUID + ".dat" + "_" + millis());
	}

	public File getLevelDat() {
		return new File(root, "level.dat" + "_" + millis());
	}

	private String millis() {
		return new Timestamp(System.currentTimeMillis())
				.toString()
				.replace(" ", "_")
				.replace(":", "-")
				.replace(".", "_");
	}

	public boolean tryBackupPlayersFile(File from, String playerName) {
		return tryBackup(players, from, getPlayersFile(playerName));
	}

	public boolean tryBackupPlayerdataFile(File from, String playerUUID) {
		return tryBackup(playerdata, from, getPlayerdataFile(playerUUID));
	}

	public boolean tryBackupLevelDat(File from) {
		return tryBackup(root, from, getLevelDat());
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
