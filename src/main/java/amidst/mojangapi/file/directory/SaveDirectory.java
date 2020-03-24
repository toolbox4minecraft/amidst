package amidst.mojangapi.file.directory;

import java.nio.file.Files;
import java.nio.file.Path;

import amidst.documentation.Immutable;

@Immutable
public class SaveDirectory {
	private final Path root;
	private final SaveAmidstDirectory amidst;
	private final Path players;
	private final Path playerdata;
	private final Path levelDat;

	public SaveDirectory(Path root) {
		this.root = root;
		this.amidst = new SaveAmidstDirectory(root.resolve("amidst"));
		this.players = root.resolve("players");
		this.playerdata = root.resolve("playerdata");
		this.levelDat = root.resolve("level.dat");
	}

	public boolean isValid() {
		return Files.isDirectory(root) && Files.isRegularFile(levelDat);
	}

	public boolean hasMultiplayerPlayers() {
		return Files.isDirectory(playerdata) || Files.isDirectory(players);
	}

	public Path getRoot() {
		return root;
	}

	public SaveAmidstDirectory getAmidst() {
		return amidst;
	}

	public Path getPlayers() {
		return players;
	}

	public Path getPlayerdata() {
		return playerdata;
	}

	public Path getLevelDat() {
		return levelDat;
	}

	public Path getPlayersFile(String playerName) {
		return players.resolve(playerName + ".dat");
	}

	public Path getPlayerdataFile(String playerUUID) {
		return playerdata.resolve(playerUUID + ".dat");
	}
}
