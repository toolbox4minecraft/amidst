package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.minecraft.world.FileWorld.Player;

public class MapObjectPlayer extends MapObject {
	private Player player;
	private BufferedImage marker;
	private Fragment parentFragment;

	public MapObjectPlayer(Player player) {
		super(MapMarkers.PLAYER, toFragmentCoordinates(player.getX()),
				toFragmentCoordinates(player.getZ()));
		this.player = player;
		initMarker();
	}

	private void initMarker() {
		marker = getType().getImage();
	}

	@Override
	public int getX() {
		return toFragmentCoordinates(player.getX());
	}

	@Override
	public int getY() {
		return toFragmentCoordinates(player.getZ());
	}

	public int getWorldX() {
		return player.getX();
	}

	public int getWorldY() {
		return player.getZ();
	}

	@Override
	public int getWidth() {
		return (int) (marker.getWidth() * getLocalScale());
	}

	@Override
	public int getHeight() {
		return (int) (marker.getHeight() * getLocalScale());
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

	public Fragment getParentFragment() {
		return parentFragment;
	}

	public void setParentFragment(Fragment parentFragment) {
		this.parentFragment = parentFragment;
	}
}
