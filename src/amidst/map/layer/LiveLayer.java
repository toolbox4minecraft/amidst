package amidst.map.layer;

import amidst.map.Fragment;

public abstract class LiveLayer extends Layer {
	public LiveLayer(LayerType layerType) {
		super(layerType);
	}

	@Override
	public void load(Fragment fragment, int[] imageCache) {
		// noop
	}

	@Override
	public void reload(Fragment fragment, int[] imageCache) {
		// noop
	}
}
