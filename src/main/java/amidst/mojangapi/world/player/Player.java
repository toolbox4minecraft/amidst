package amidst.mojangapi.world.player;

import java.io.IOException;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.file.MojangApiParsingException;
import amidst.mojangapi.file.nbt.player.PlayerNbt;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIconImage;

@ThreadSafe
public class Player {
	private final PlayerInformation playerInformation;
	private final PlayerNbt playerNbt;
	private volatile PlayerCoordinates savedCoordinates;
	private volatile PlayerCoordinates currentCoordinates;

	public Player(PlayerInformation playerInformation, PlayerNbt playerNbt) {
		this.playerInformation = playerInformation;
		this.playerNbt = playerNbt;
	}

	public String getPlayerName() {
		return playerInformation.getNameOrUUID();
	}

	public WorldIconImage getHead() {
		return playerInformation.getHead();
	}

	public PlayerCoordinates getPlayerCoordinates() {
		return currentCoordinates;
	}

	public void moveTo(CoordinatesInWorld coordinates, long height, Dimension dimension) {
		this.currentCoordinates = new PlayerCoordinates(coordinates, height, dimension);
	}

	public boolean trySaveLocation() {
		try {
			if (saveLocation()) {
				return true;
			} else {
				Log.w("skipping to save player location, because the backup file cannot be created for player: "
						+ getPlayerName());
				return false;
			}
		} catch (MojangApiParsingException e) {
			Log.w("error while writing player location for player: " + getPlayerName());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns true if the player was not moved or the new location was
	 * successfully saved.
	 */
	public synchronized boolean saveLocation() throws MojangApiParsingException {
		PlayerCoordinates currentCoordinates = this.currentCoordinates;
		if (savedCoordinates != currentCoordinates) {
			if (playerNbt.tryWriteCoordinates(currentCoordinates)) {
				savedCoordinates = currentCoordinates;
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public boolean tryLoadLocation() {
		try {
			loadLocation();
			return true;
		} catch (IOException | MojangApiParsingException e) {
			Log.w("error while reading player location for player: " + getPlayerName());
			e.printStackTrace();
			return false;
		}
	}

	public synchronized void loadLocation() throws IOException, MojangApiParsingException {
		this.savedCoordinates = playerNbt.readCoordinates();
		this.currentCoordinates = savedCoordinates;
	}
}
