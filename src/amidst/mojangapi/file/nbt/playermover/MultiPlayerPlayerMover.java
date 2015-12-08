package amidst.mojangapi.file.nbt.playermover;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.world.Player;

public class MultiPlayerPlayerMover extends PlayerMover {
	private final SaveDirectory saveDirectory;

	public MultiPlayerPlayerMover(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	protected boolean tryBackup(Player player) {
		return saveDirectory.tryBackupPlayersFile(player.getPlayerName());
	}

	@Override
	protected void doMovePlayer(Player player) throws FileNotFoundException,
			IOException {
		movePlayerOnMultiPlayerWorld(player,
				saveDirectory.getPlayersFile(player.getPlayerName()));
	}
}
