package amidst.mojangapi.file.directory;

import java.io.File;

import amidst.documentation.Immutable;

@Immutable
public class SaveDirectory {
	private final File root;
	private final SaveAmidstDirectory amidst;
	private final File players;
	private final File playerdata;
	private final File levelDat;

	public SaveDirectory(File root) {
		this.root = root;
		this.amidst = new SaveAmidstDirectory(new File(root, "amidst"));
		this.players = new File(root, "players");
		this.playerdata = new File(root, "playerdata");
		this.levelDat = new File(root, "level.dat");
	}

	public boolean isValid() {
		return root.isDirectory() && levelDat.isFile();
	}

	public boolean hasMultiplayerPlayers() {
		return playerdata.isDirectory() || players.isDirectory();
	}

	public File getRoot() {
		return root;
	}

	public SaveAmidstDirectory getAmidst() {
		return amidst;
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

	public File getPlayersFile(String playerName) {
		return new File(players, playerName + ".dat");
	}

	public File getPlayerdataFile(String playerUUID) {
		return new File(playerdata, playerUUID + ".dat");
	}
}
