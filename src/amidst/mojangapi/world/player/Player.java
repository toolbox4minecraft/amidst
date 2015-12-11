package amidst.mojangapi.world.player;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;
import amidst.mojangapi.file.nbt.playerfile.PlayerFile;
import amidst.mojangapi.world.CoordinatesInWorld;
import amidst.mojangapi.world.icon.DefaultWorldIconTypes;

@ThreadSafe
public class Player {
	private static final String NAMELESS_PLAYER_NAME = "The Singleplayer Player";

	public static Player nameless(PlayerFile playerFile) {
		return new Player(NAMELESS_PLAYER_NAME, false, playerFile);
	}

	public static Player named(String playerName, PlayerFile playerFile) {
		return new Player(playerName, true, playerFile);
	}

	private final String playerName;

	private final boolean isSkinLoadable;
	private volatile BufferedImage skin = DefaultWorldIconTypes.PLAYER
			.getImage();

	private final PlayerFile playerFile;
	private volatile PlayerCoordinates savedCoordinates;
	private volatile PlayerCoordinates currentCoordinates;

	private Player(String playerName, boolean isSkinLoadable,
			PlayerFile playerFile) {
		this.playerName = playerName;
		this.isSkinLoadable = isSkinLoadable;
		this.playerFile = playerFile;
	}

	public String getPlayerName() {
		return playerName;
	}

	public boolean isSkinLoadable() {
		return isSkinLoadable;
	}

	public BufferedImage getSkin() {
		return skin;
	}

	public void setSkin(BufferedImage skin) {
		this.skin = skin;
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
						+ playerName);
				return false;
			}
		} catch (IOException e) {
			Log.w("error while writing player location for player: "
					+ playerName);
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
					+ playerName);
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
