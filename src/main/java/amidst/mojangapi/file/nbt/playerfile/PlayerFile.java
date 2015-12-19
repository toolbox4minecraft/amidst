package amidst.mojangapi.file.nbt.playerfile;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.player.Player;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.mojangapi.world.player.PlayerInformationCache;

@Immutable
public abstract class PlayerFile {
	public boolean tryWriteCoordinates(PlayerCoordinates coordinates)
			throws FileNotFoundException, IOException {
		if (tryBackup()) {
			doWriteCoordinates(coordinates);
			return true;
		} else {
			return false;
		}
	}

	protected abstract boolean tryBackup();

	protected abstract void doWriteCoordinates(PlayerCoordinates coordinates)
			throws FileNotFoundException, IOException;

	public abstract PlayerCoordinates readCoordinates()
			throws FileNotFoundException, IOException;

	public abstract Player createPlayer(PlayerInformationCache cache);
}
