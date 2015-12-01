package amidst.fragment.layer;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.loader.FragmentLoader;
import amidst.map.Fragment;

public class LayerManager {
	private final List<AtomicBoolean> invalidatedLayers;
	private final List<LayerDeclaration> declarations;
	private final List<FragmentLoader> loaders;
	private final List<FragmentDrawer> drawers;

	public LayerManager(List<AtomicBoolean> invalidatedLayers,
			List<LayerDeclaration> declarations, List<FragmentLoader> loaders,
			List<FragmentDrawer> drawers) {
		this.invalidatedLayers = invalidatedLayers;
		this.declarations = declarations;
		this.loaders = loaders;
		this.drawers = drawers;
	}

	public List<LayerDeclaration> getLayerDeclarations() {
		return declarations;
	}

	public List<FragmentDrawer> getFragmentDrawers() {
		return drawers;
	}

	public void clearInvalidatedLayers() {
		for (AtomicBoolean invalidatedLayer : invalidatedLayers) {
			invalidatedLayer.set(false);
		}
	}

	public void invalidateLayer(int layerId) {
		invalidatedLayers.get(layerId).set(true);
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
		return invalidatedLayers.get(layerDeclaration.getLayerId()).get();
	}
}
