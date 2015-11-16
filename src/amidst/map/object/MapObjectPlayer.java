package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.Fragment;
import amidst.map.MapMarkers;
import amidst.minecraft.world.FileWorld.Player;

public class MapObjectPlayer extends MapObject {
	private Player player;
	private BufferedImage image;
	private Fragment parentFragment;

	public MapObjectPlayer(Player player) {
		super(MapMarkers.PLAYER, toFragmentCoordinates(player.getX()),
				toFragmentCoordinates(player.getZ()));
		this.player = player;
		this.image = MapMarkers.PLAYER.getImage();
	}

	public Fragment getParentFragment() {
		return parentFragment;
	}

	public void setParentFragment(Fragment parentFragment) {
		this.parentFragment = parentFragment;
	}

	@Deprecated
	public void setPosition(int x, int z) {
		player.moveTo(x, z);
	}

	public int getWorldX() {
		return player.getX();
	}

	public int getWorldY() {
		return player.getZ();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}

	@Override
	public int getX() {
		return toFragmentCoordinates(player.getX());
	}

	@Override
	public int getY() {
		return toFragmentCoordinates(player.getZ());
	}

	@Override
	public String getName() {
		return player.getPlayerName();
	}
}
