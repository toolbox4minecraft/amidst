package amidst.mojangapi.world;

import java.awt.image.BufferedImage;

import amidst.mojangapi.world.icon.DefaultWorldIconTypes;

public class Player {
	private final String playerName;

	private volatile CoordinatesInWorld coordinates;
	private volatile boolean isMoved = false;

	private volatile BufferedImage skin = DefaultWorldIconTypes.PLAYER
			.getImage();

	public Player(String playerName, CoordinatesInWorld coordinates) {
		this.playerName = playerName;
		this.coordinates = coordinates;
	}

	public String getPlayerName() {
		return playerName;
	}

	public CoordinatesInWorld getCoordinates() {
		return coordinates;
	}

	public BufferedImage getSkin() {
		return skin;
	}

	public void setSkin(BufferedImage skin) {
		this.skin = skin;
	}

	public void moveTo(CoordinatesInWorld coordinates) {
		this.coordinates = coordinates;
		isMoved = true;
	}

	public boolean getAndResetIsMoved() {
		boolean result = isMoved;
		isMoved = false;
		return result;
	}
}
