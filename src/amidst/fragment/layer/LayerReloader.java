package amidst.fragment.layer;

import amidst.map.FragmentManager;

public class LayerReloader {
	private final FragmentManager fragmentManager;

	public LayerReloader(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
	}

	public void reloadBiomeLayer() {
		fragmentManager.reloadLayer(LayerIds.BIOME);
	}

	public void reloadPlayerLayer() {
		fragmentManager.reloadLayer(LayerIds.PLAYER);
	}
}
