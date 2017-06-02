package amidst.mojangapi.file.directory;

import java.io.File;

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
}
