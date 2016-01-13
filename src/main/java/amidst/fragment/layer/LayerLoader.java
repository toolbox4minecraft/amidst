package amidst.fragment.layer;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledByAny;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.loader.FragmentLoader;
import amidst.mojangapi.world.Dimension;

@NotThreadSafe
public class LayerLoader {
	private final Iterable<FragmentLoader> loaders;
	private final boolean[] invalidatedLayers;

	@CalledByAny
	public LayerLoader(Iterable<FragmentLoader> loaders, int numberOfLayers) {
		this.loaders = loaders;
		this.invalidatedLayers = new boolean[numberOfLayers];
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void clearInvalidatedLayers() {
		for (int i = 0; i < invalidatedLayers.length; i++) {
			invalidatedLayers[i] = false;
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void invalidateLayer(int layerId) {
		invalidatedLayers[layerId] = true;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void loadAll(Dimension dimension, Fragment fragment) {
		for (FragmentLoader loader : loaders) {
			if (loader.isEnabled()) {
				loader.load(dimension, fragment);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	public void reloadInvalidated(Dimension dimension, Fragment fragment) {
		for (FragmentLoader loader : loaders) {
			if (loader.isEnabled() && isInvalidated(loader.getLayerId())) {
				loader.reload(dimension, fragment);
			}
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private boolean isInvalidated(int layerId) {
		return invalidatedLayers[layerId];
	}
}
