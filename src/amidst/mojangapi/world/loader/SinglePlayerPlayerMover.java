package amidst.mojangapi.world.loader;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.world.Player;

public class SinglePlayerPlayerMover extends PlayerMover {
	private final SaveDirectory saveDirectory;

	public SinglePlayerPlayerMover(SaveDirectory saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	protected boolean tryBackup(Player player) {
		return saveDirectory.tryBackupLevelDat();
	}

	@Override
	protected void doMovePlayer(Player player) throws FileNotFoundException,
			IOException {
		movePlayerOnSinglePlayerWorld(player, saveDirectory.getLevelDat());
	}
}
