package amidst.mojangapi.dotminecraft;

import java.io.File;

public class SavesDirectory {
	private final File saves;
	private final File players;
	private final File playerdata;
	private final File levelDat;

	public SavesDirectory(File saves) {
		this.saves = saves;
		this.players = new File(saves, "players");
		this.playerdata = new File(saves, "playerdata");
		this.levelDat = new File(saves, "level.dat");
	}

	public File getSaves() {
		return saves;
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
