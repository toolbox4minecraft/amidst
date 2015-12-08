package amidst.mojangapi.world;

import java.awt.image.BufferedImage;

import amidst.mojangapi.world.icon.DefaultWorldIconTypes;

public class Player {
	private volatile BufferedImage skin = DefaultWorldIconTypes.PLAYER
			.getImage();

	private final String playerName;
	private volatile long x;
	private volatile long y;
	private volatile long z;

	private volatile boolean isMoved = false;

	public Player(String playerName, double x, double y, double z) {
		this.playerName = playerName;
		this.x = (long) x;
		this.y = (long) y;
		this.z = (long) z;
	}

	public String getPlayerName() {
		return playerName;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public long getZ() {
		return z;
	}

	public CoordinatesInWorld createCoordinates() {
		return CoordinatesInWorld.from(x, z);
	}

	public BufferedImage getSkin() {
		return skin;
	}

	public void setSkin(BufferedImage skin) {
		this.skin = skin;
	}

	public void moveTo(CoordinatesInWorld coordinates, long height) {
		this.x = coordinates.getX();
		this.y = height;
		this.z = coordinates.getY();
		isMoved = true;
	}

	public boolean getAndResetIsMoved() {
		boolean result = isMoved;
		isMoved = false;
		return result;
	}
}
