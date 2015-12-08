package amidst.mojangapi.file.directory;

import java.io.File;

public class SaveDirectory {
	private final File root;
	private final File players;
	private final File playerdata;
	private final File levelDat;

	public SaveDirectory(File root) {
		this.root = root;
		this.players = new File(root, "players");
		this.playerdata = new File(root, "playerdata");
		this.levelDat = new File(root, "level.dat");
	}

	public boolean isValid() {
		return root.isDirectory() && levelDat.isFile()
				&& (players.isDirectory() || playerdata.isDirectory());
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

	public File getLevelDat() {
		return levelDat;
	}
}
