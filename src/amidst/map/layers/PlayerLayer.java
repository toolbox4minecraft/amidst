package amidst.map.layers;

import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectPlayer;
import amidst.map.SkinLoader;
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
				if (isInFragmentBounds(fragment, player)) {
					player.parentLayer = this;
					player.parentFragment = fragment;
					fragment.addObject(player);
				}
			}
		}
	}

	private boolean isInFragmentBounds(Fragment frag, MapObjectPlayer player) {
		return player.getGlobalX() >= frag.getBlockX()
				&& player.getGlobalX() < frag.getBlockX() + Fragment.SIZE
				&& player.getGlobalY() >= frag.getBlockY()
				&& player.getGlobalY() < frag.getBlockY() + Fragment.SIZE;
	}

	@Override
	public void clearMapObjects(Fragment fragment) {
		for (int i = 0; i < fragment.getObjectsLength(); i++) {
			if (fragment.getObjects()[i] instanceof MapObjectPlayer) {
				MapObjectPlayer mapObjectPlayer = (MapObjectPlayer) fragment
						.getObjects()[i];
				mapObjectPlayer.parentFragment = null;
			}
		}
		super.clearMapObjects(fragment);
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
