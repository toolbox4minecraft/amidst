package amidst.mojangapi.dotminecraft;

import java.io.File;

public class SaveDirectory {
	private final File worldSave;
	private final File players;
	private final File playerdata;
	private final File levelDat;

	public SaveDirectory(File worldSave) {
		this.worldSave = worldSave;
		this.players = new File(worldSave, "players");
		this.playerdata = new File(worldSave, "playerdata");
		this.levelDat = new File(worldSave, "level.dat");
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
