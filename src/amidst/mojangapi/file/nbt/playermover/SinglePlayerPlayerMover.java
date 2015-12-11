package amidst.mojangapi.file.nbt.playermover;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.directory.SaveDirectory;
import amidst.mojangapi.file.nbt.PlayerLocationSaver;
import amidst.mojangapi.world.Player;
import amidst.mojangapi.world.PlayerCoordinates;

@Immutable
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
	protected void doMovePlayer(Player player, PlayerCoordinates coordinates)
			throws FileNotFoundException, IOException {
		PlayerLocationSaver.movePlayerOnSinglePlayerWorld(coordinates,
				saveDirectory.getLevelDat());
	}
}
