package amidst.map.object;

import java.awt.image.BufferedImage;

import amidst.minecraft.world.FileWorld.Player;
import amidst.preferences.BooleanPrefModel;

public class MapObjectPlayer extends MapObject {
	private Player player;

	public MapObjectPlayer(BooleanPrefModel isVisiblePreference, Player player) {
		super(isVisiblePreference, player.getCoordinates(), player
				.getPlayerName(), player.getSkin());
		this.player = player;
	}

	// TODO: remove old and add new map object after skin is loaded?
	@Override
	public BufferedImage getImage() {
		return player.getSkin();
	}
}
