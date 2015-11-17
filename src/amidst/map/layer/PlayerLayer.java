package amidst.map.layer;

import java.util.Collections;
import java.util.List;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.SkinLoader;
import amidst.map.object.MapObjectPlayer;
import amidst.minecraft.world.FileWorld;
import amidst.minecraft.world.World;

public class PlayerLayer extends IconLayer {
	private SkinLoader skinLoader;
	private List<MapObjectPlayer> players;

	public PlayerLayer(SkinLoader skinLoader) {
		this.skinLoader = skinLoader;
	}

	@Override
	public boolean isVisible() {
		return Options.instance.showPlayers.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		for (MapObjectPlayer player : players) {
			if (fragment.isInBounds(player)) {
				player.setFragment(fragment);
			}
		}
	}

	public void setWorld(World world) {
		if (world instanceof FileWorld) {
			players = world.getAsFileWorld().getMapObjectPlayers(
					Options.instance.showPlayers);
			loadSkins();
		} else {
			players = Collections.emptyList();
		}
	}

	private void loadSkins() {
		for (MapObjectPlayer player : players) {
			skinLoader.loadSkin(player);
		}
	}

	@Deprecated
	public List<MapObjectPlayer> getPlayers() {
		return players;
	}
}
