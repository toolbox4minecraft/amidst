package amidst.mojangapi.world.player;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.file.nbt.playerfile.PlayerFile;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;

@ThreadSafe
public class Player {
	private final PlayerInformation playerInformation;
	private final PlayerFile playerFile;
	private volatile PlayerCoordinates savedCoordinates;
	private volatile PlayerCoordinates currentCoordinates;

	public Player(PlayerInformation playerInformation, PlayerFile playerFile) {
		this.playerInformation = playerInformation;
		this.playerFile = playerFile;
	}

	public String getPlayerName() {
		return playerInformation.getNameOrUUID();
	}

	public BufferedImage getHead() {
		return playerInformation.getHead();
	}

	public PlayerCoordinates getPlayerCoordinates() {
		return currentCoordinates;
	}

	public void moveTo(CoordinatesInWorld coordinates, long height) {
		this.currentCoordinates = new PlayerCoordinates(coordinates, height);
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
		} catch (IOException e) {
			Log.w("error while writing player location for player: "
					+ getPlayerName());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns true if the player was not moved or the new location was
	 * successfully saved.
	 */
	public synchronized boolean saveLocation() throws FileNotFoundException,
			IOException {
		PlayerCoordinates currentCoordinates = this.currentCoordinates;
		if (savedCoordinates != currentCoordinates) {
			if (playerFile.tryWriteCoordinates(currentCoordinates)) {
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
		} catch (IOException e) {
			Log.w("error while reading player location for player: "
					+ getPlayerName());
			e.printStackTrace();
			return false;
		}
	}

	public synchronized void loadLocation() throws FileNotFoundException,
			IOException {
		this.savedCoordinates = playerFile.readCoordinates();
		this.currentCoordinates = savedCoordinates;
	}
}
