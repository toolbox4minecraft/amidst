package amidst.fragment.layer;

import amidst.fragment.loader.FragmentLoader;
import amidst.map.Fragment;

public class LayerLoader {
	private final Iterable<FragmentLoader> loaders;
	private final boolean[] invalidatedLayers;

	public LayerLoader(Iterable<FragmentLoader> loaders, int numberOfLayers) {
		this.loaders = loaders;
		this.invalidatedLayers = new boolean[numberOfLayers];
	}

	public void clearInvalidatedLayers() {
		for (int i = 0; i < invalidatedLayers.length; i++) {
			invalidatedLayers[i] = false;
		}
	}

	public void invalidateLayer(int layerId) {
		invalidatedLayers[layerId] = true;
	}

	public void loadAll(Fragment fragment) {
		for (FragmentLoader loader : loaders) {
			loader.load(fragment);
		}
	}

	public void reloadInvalidated(Fragment fragment) {
		for (FragmentLoader loader : loaders) {
			if (isInvalidated(loader.getLayerDeclaration())) {
				loader.reload(fragment);
			}
		}
	}

	private boolean isInvalidated(LayerDeclaration layerDeclaration) {
		return invalidatedLayers[layerDeclaration.getLayerId()];
	}
}
