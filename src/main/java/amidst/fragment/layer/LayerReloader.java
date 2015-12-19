package amidst.fragment.layer;

import amidst.documentation.ThreadSafe;
import amidst.fragment.FragmentQueueProcessor;
import amidst.mojangapi.world.World;

@ThreadSafe
public class LayerReloader {
	private final World world;
	private final FragmentQueueProcessor fragmentQueueProcessor;

	public LayerReloader(World world,
			FragmentQueueProcessor fragmentQueueProcessor) {
		this.world = world;
		this.fragmentQueueProcessor = fragmentQueueProcessor;
	}

	public void reloadBiomeLayer() {
		fragmentQueueProcessor.invalidateLayer(LayerIds.BIOME);
	}

	public void reloadPlayerLayer() {
		world.reloadPlayerWorldIcons();
		fragmentQueueProcessor.invalidateLayer(LayerIds.PLAYER);
	}
}
