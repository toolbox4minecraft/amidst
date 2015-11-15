package amidst.map.layers;

import MoF.SkinLoader;
import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectPlayer;
import amidst.minecraft.world.FileWorld;
import amidst.minecraft.world.World;

public class PlayerLayer extends IconLayer {
	// TODO: make this non-static
	private static SkinLoader skinManager = new SkinLoader();

	static {
		skinManager.start();
	}

	private FileWorld world;

	@Override
	public boolean isVisible() {
		return Options.instance.showPlayers.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		if (world != null) {
			for (MapObjectPlayer player : world.getMapObjectPlayers()) {
				if (isPlayerInFragment(fragment, player)) {
					player.parentLayer = this;
					player.parentFragment = fragment;
					fragment.addObject(player);
				}
			}
		}
	}

	private boolean isPlayerInFragment(Fragment frag, MapObjectPlayer player) {
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
			this.world = (FileWorld) world;
			updateSkinManager();
		} else {
			this.world = null;
		}
	}

	private void updateSkinManager() {
		for (MapObjectPlayer player : world.getMapObjectPlayers()) {
			skinManager.addPlayer(player);
		}
	}
}
