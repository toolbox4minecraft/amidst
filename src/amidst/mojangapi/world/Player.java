package amidst.mojangapi.world;

import java.awt.image.BufferedImage;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.icon.DefaultWorldIconTypes;

@ThreadSafe
public class Player {
	private static final String NAMELESS_PLAYER_NAME = "Player";

	public static Player named(String playerName,
			PlayerCoordinates playerCoordinates) {
		return new Player(playerName, true, playerCoordinates);
	}

	public static Player nameless(PlayerCoordinates playerCoordinates) {
		return new Player(NAMELESS_PLAYER_NAME, false, playerCoordinates);
	}

	private final String playerName;

	private final boolean isSkinLoadable;
	private volatile BufferedImage skin = DefaultWorldIconTypes.PLAYER
			.getImage();

	private volatile PlayerCoordinates savedCoordinates;
	private volatile PlayerCoordinates currentCoordinates;

	private Player(String playerName, boolean isSkinLoadable,
			PlayerCoordinates playerCoordinates) {
		this.playerName = playerName;
		this.isSkinLoadable = isSkinLoadable;
		this.savedCoordinates = playerCoordinates;
		this.currentCoordinates = playerCoordinates;
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

	public PlayerCoordinates getAndSetCurrentCoordinatesIfMoved() {
		if (savedCoordinates != currentCoordinates) {
			savedCoordinates = currentCoordinates;
			return savedCoordinates;
		} else {
			return null;
		}
	}
}
