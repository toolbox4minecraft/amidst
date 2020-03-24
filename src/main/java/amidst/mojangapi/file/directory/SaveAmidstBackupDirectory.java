package amidst.mojangapi.file.directory;

import java.nio.file.Path;

public class SaveAmidstBackupDirectory {
	private final Path root;
	private final Path players;
	private final Path playerdata;

	public SaveAmidstBackupDirectory(Path root) {
		this.root = root;
		this.players = root.resolve("players");
		this.playerdata = root.resolve("playerdata");
	}

	public Path getRoot() {
		return root;
	}

	public Path getPlayers() {
		return players;
	}

	public Path getPlayerdata() {
		return playerdata;
	}
}
