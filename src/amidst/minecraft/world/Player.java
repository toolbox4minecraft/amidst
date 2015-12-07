package amidst.minecraft.world;

import java.awt.image.BufferedImage;

import amidst.minecraft.world.icon.DefaultWorldIconTypes;

public class Player {
	private final PlayerMover playerMover;
	private boolean isMoved = false;

	private BufferedImage skin = DefaultWorldIconTypes.PLAYER.getImage();
	private final String playerName;
	private CoordinatesInWorld coordinates;

	public Player(PlayerMover playerMover, String playerName,
			CoordinatesInWorld coordinates) {
		this.playerMover = playerMover;
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

	@Deprecated
	public void saveLocation() {
		if (isMoved) {
			playerMover.movePlayer(this);
			isMoved = false;
		}
	}
}
