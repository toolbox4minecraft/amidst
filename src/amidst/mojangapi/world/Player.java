package amidst.mojangapi.world;

import java.awt.image.BufferedImage;

import amidst.mojangapi.world.icon.DefaultWorldIconTypes;

public class Player {
	private static final String NAMELESS_PLAYER_NAME = "Player";

	public static Player named(String playerName, double x, double y, double z) {
		return new Player(playerName, true, x, y, z);
	}

	public static Player nameless(double x, double y, double z) {
		return new Player(NAMELESS_PLAYER_NAME, false, x, y, z);
	}

	private volatile BufferedImage skin = DefaultWorldIconTypes.PLAYER
			.getImage();

	private final String playerName;
	private final boolean isSkinLoadable;
	private volatile long x;
	private volatile long y;
	private volatile long z;

	private volatile boolean isMoved = false;

	private Player(String playerName, boolean isSkinLoadable, double x,
			double y, double z) {
		this.playerName = playerName;
		this.isSkinLoadable = isSkinLoadable;
		this.x = (long) x;
		this.y = (long) y;
		this.z = (long) z;
	}

	public String getPlayerName() {
		return playerName;
	}

	public boolean isSkinLoadable() {
		return isSkinLoadable;
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
