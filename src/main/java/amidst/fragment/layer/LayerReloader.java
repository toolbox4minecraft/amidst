package amidst.fragment.layer;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.World;

@ThreadSafe
public class LayerReloader {
	private final World world;
	private final LayerManager layerManager;

	public LayerReloader(World world, LayerManager layerManager) {
		this.world = world;
		this.layerManager = layerManager;
	}

	public void reloadBackgroundLayer() {
		layerManager.invalidateLayer(LayerIds.BACKGROUND);
	}

	public void reloadPlayerLayer() {
		world.reloadPlayerWorldIcons();
		layerManager.invalidateLayer(LayerIds.PLAYER);
	}
}
