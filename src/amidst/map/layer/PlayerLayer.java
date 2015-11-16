package amidst.map.layer;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.SkinLoader;
import amidst.map.object.MapObjectPlayer;
import amidst.minecraft.world.FileWorld;
import amidst.minecraft.world.World;

public class PlayerLayer extends IconLayer {
	private SkinLoader skinLoader;
	private FileWorld worldFile;

	public PlayerLayer(SkinLoader skinLoader) {
		this.skinLoader = skinLoader;
	}

	@Override
	public boolean isVisible() {
		return Options.instance.showPlayers.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		if (worldFile != null) {
			for (MapObjectPlayer player : worldFile.getMapObjectPlayers()) {
				if (fragment.isInBounds(player)) {
					player.setIconLayer(this);
					player.setFragment(fragment);
				}
			}
		}
	}

	public void setWorld(World world) {
		if (world instanceof FileWorld) {
			this.worldFile = (FileWorld) world;
			updateSkinManager();
		} else {
			this.worldFile = null;
		}
	}

	private void updateSkinManager() {
		for (MapObjectPlayer player : worldFile.getMapObjectPlayers()) {
			skinLoader.loadSkin(player);
		}
	}
}
