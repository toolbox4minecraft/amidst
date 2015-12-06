package amidst.mojangapi.dotminecraft;

import java.io.File;

public class SaveDirectory {
	private final File worldSave;
	private final File players;
	private final File playerdata;
	private final File levelDat;

	public SaveDirectory(File worldSave, File players, File playerdata,
			File levelDat) {
		this.worldSave = worldSave;
		this.players = players;
		this.playerdata = playerdata;
		this.levelDat = levelDat;
	}

	public File getWorldSave() {
		return worldSave;
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
