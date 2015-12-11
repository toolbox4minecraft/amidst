package amidst.mojangapi.file.nbt.playermover;

import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.Immutable;
import amidst.logging.Log;
import amidst.mojangapi.world.Player;
import amidst.mojangapi.world.PlayerCoordinates;

@Immutable
public abstract class PlayerMover {
	// TODO: gui feedback
	public void movePlayer(Player player) {
		PlayerCoordinates coordinates = player
				.getAndSetCurrentCoordinatesIfMoved();
		if (coordinates == null) {
			// noop
		} else if (tryBackup(player)) {
			try {
				doMovePlayer(player, coordinates);
			} catch (Exception e) {
				Log.w("Creation of backup file failed. Skipping player movement for player: "
						+ player.getPlayerName());
				e.printStackTrace();
			}
		} else {
			Log.w("Creation of backup file failed. Skipping player movement for player: "
					+ player.getPlayerName());
		}
	}

	protected abstract boolean tryBackup(Player player);

	protected abstract void doMovePlayer(Player player,
			PlayerCoordinates coordinates) throws FileNotFoundException,
			IOException;
}
