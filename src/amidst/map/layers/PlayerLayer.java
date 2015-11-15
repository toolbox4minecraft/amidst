package amidst.map.layers;

import MoF.SkinManager;
import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectPlayer;
import amidst.minecraft.world.World;

public class PlayerLayer extends IconLayer {
	private static SkinManager skinManager = new SkinManager();

	static {
		skinManager.start();
	}

	private World world;
	public boolean isEnabled;

	@Override
	public boolean isVisible() {
		return Options.instance.showPlayers.get();
	}

	@Override
	public void generateMapObjects(Fragment fragment) {
		if (!isEnabled) {
			return;
		}
		for (MapObjectPlayer player : world.getMapObjectPlayers()) {
			if (isPlayerInFragment(fragment, player)) {
				player.parentLayer = this;
				player.parentFragment = fragment;
				fragment.addObject(player);
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
		this.world = world;
		for (MapObjectPlayer player : this.world.getMapObjectPlayers()) {
			skinManager.addPlayer(player);
		}
	}
}
