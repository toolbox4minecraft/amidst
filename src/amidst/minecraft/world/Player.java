package amidst.minecraft.world;

import java.awt.image.BufferedImage;

import amidst.minecraft.world.icon.DefaultWorldIconTypes;

public class Player {
	private PlayerMover playerMover;
	private boolean isMoved = false;

	private BufferedImage skin = DefaultWorldIconTypes.PLAYER.getImage();
	private String playerName;
	private CoordinatesInWorld coordinates;

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

	@Deprecated
	public void setMover(PlayerMover mover) {
		this.playerMover = mover;
	}

	public void moveTo(CoordinatesInWorld coordinates) {
		this.coordinates = coordinates;
		isMoved = true;
	}

	public void saveLocation() {
		if (isMoved) {
			playerMover.movePlayer(this);
			isMoved = false;
		}
	}
}
