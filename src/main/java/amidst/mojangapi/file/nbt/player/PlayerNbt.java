package amidst.mojangapi.file.nbt.player;

import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.world.player.Player;
import amidst.mojangapi.world.player.PlayerCoordinates;
import amidst.mojangapi.world.player.PlayerInformationCache;

@Immutable
public abstract class PlayerNbt {
	public boolean tryWriteCoordinates(PlayerCoordinates coordinates) throws MojangApiParsingException {
		if (tryBackup()) {
			doWriteCoordinates(coordinates);
			return true;
		} else {
			return false;
		}
	}

	protected abstract boolean tryBackup();

	protected abstract void doWriteCoordinates(PlayerCoordinates coordinates) throws MojangApiParsingException;

	public abstract PlayerCoordinates readCoordinates() throws IOException, MojangApiParsingException;

	public abstract Player createPlayer(PlayerInformationCache cache);
}
