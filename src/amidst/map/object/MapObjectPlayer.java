package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.MapMarkers;
import amidst.map.layer.IconLayer;
import amidst.minecraft.world.FileWorld.Player;
import amidst.utilities.CoordinateUtils;

public class MapObjectPlayer extends MapObject {
	private Player player;
	private BufferedImage image;

	public MapObjectPlayer(IconLayer iconLayer, Player player) {
		super(iconLayer, MapMarkers.PLAYER, CoordinateUtils.toFragment(player
				.getX()), CoordinateUtils.toFragment(player.getZ()));
		this.player = player;
		this.image = MapMarkers.PLAYER.getImage();
	}

	@Deprecated
	public void setPosition(int x, int z) {
		player.moveTo(x, z);
	}

	@Override
	public int getXInWorld() {
		return player.getX();
	}

	@Override
	public int getYInWorld() {
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
	public int getXInFragment() {
		return CoordinateUtils.toFragment(player.getX());
	}

	@Override
	public int getYInFragment() {
		return CoordinateUtils.toFragment(player.getZ());
	}

	@Override
	public String getName() {
		return player.getPlayerName();
	}
}
