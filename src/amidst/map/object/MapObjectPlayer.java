package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.map.MapMarkers;
import amidst.minecraft.world.FileWorld.Player;
import amidst.preferences.BooleanPrefModel;
import amidst.utilities.CoordinateUtils;

public class MapObjectPlayer extends MapObject {
	private Player player;

	public MapObjectPlayer(BooleanPrefModel isVisiblePreference, Player player) {
		super(isVisiblePreference, MapMarkers.PLAYER, CoordinateUtils
				.toFragmentRelative(player.getX()), CoordinateUtils
				.toFragmentRelative(player.getZ()));
		this.player = player;
	}

	@Override
	public int getXInWorld() {
		return player.getX();
	}

	@Override
	public int getYInWorld() {
		return player.getZ();
	}

	@Override
	public BufferedImage getImage() {
		return player.getSkin();
	}

	@Override
	public int getXInFragment() {
		return CoordinateUtils.toFragmentRelative(player.getX());
	}

	@Override
	public int getYInFragment() {
		return CoordinateUtils.toFragmentRelative(player.getZ());
	}

	@Override
	public String getName() {
		return player.getPlayerName();
	}
}
