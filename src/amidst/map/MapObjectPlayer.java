package amidst.map;

import java.awt.image.BufferedImage;

import amidst.minecraft.world.World.Player;

public class MapObjectPlayer extends MapObject {
	// TODO: understand what happens and rename the method
	private static int calc(int coordinate) {
		return calc1(coordinate) + coordinate % Fragment.SIZE;
	}

	// TODO: understand what happens and rename the method
	private static int calc1(int coordinate) {
		if (coordinate < 0) {
			return Fragment.SIZE;
		} else {
			return 0;
		}
	}

	private Player player;
	private BufferedImage marker;
	// TODO: make this private
	public Fragment parentFragment = null;

	public MapObjectPlayer(Player player) {
		super(MapMarkers.PLAYER, calc(player.getX()), calc(player.getZ()));
		this.player = player;
		initPlayerListener();
		initMarker();
	}

	private void initPlayerListener() {
		this.player.setPositionChangedListener(new Runnable() {
			@Override
			public void run() {
				updatePosition();
			}
		});
	}

	private void initMarker() {
		marker = type.image;
	}

	private void updatePosition() {
		this.x = calc(player.getX());
		this.y = calc(player.getZ());
	}

	public int getGlobalX() {
		return player.getX();
	}

	public int getGlobalY() {
		return player.getZ();
	}

	@Override
	public int getWidth() {
		return (int) (marker.getWidth() * localScale);
	}

	@Override
	public int getHeight() {
		return (int) (marker.getHeight() * localScale);
	}

	@Override
	public BufferedImage getImage() {
		return marker;
	}

	public void setMarker(BufferedImage image) {
		this.marker = image;
	}

	@Override
	public String getName() {
		return player.getPlayerName();
	}

	@Deprecated
	public void setPosition(int x, int z) {
		player.moveTo(x, z);
	}
}
