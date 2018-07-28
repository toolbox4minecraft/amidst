package amidst.mojangapi.world.player;

import amidst.documentation.ThreadSafe;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.file.SaveGamePlayer;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIconImage;

@ThreadSafe
public class Player {
	private final PlayerInformation playerInformation;
	private final SaveGamePlayer saveGamePlayer;
	private volatile PlayerCoordinates savedCoordinates;
	private volatile PlayerCoordinates currentCoordinates;

	public Player(PlayerInformation playerInformation, SaveGamePlayer saveGamePlayer) {
		this.playerInformation = playerInformation;
		this.saveGamePlayer = saveGamePlayer;
		this.savedCoordinates = saveGamePlayer.getPlayerCoordinates();
		this.currentCoordinates = savedCoordinates;
	}

	public String getPlayerName() {
		return playerInformation.getNameOrElseUUID();
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

	/**
	 * Returns true if the player was not moved or the new location was
	 * successfully saved.
	 */
	public synchronized boolean trySaveLocation() {
		PlayerCoordinates currentCoordinates = this.currentCoordinates;
		if (savedCoordinates != currentCoordinates) {
			if (saveGamePlayer.tryBackupAndWritePlayerCoordinates(currentCoordinates)) {
				savedCoordinates = currentCoordinates;
				return true;
			} else {
				AmidstLogger.warn("error while writing player location for player: {}", getPlayerName());
				return false;
			}
		} else {
			return true;
		}
	}
}
