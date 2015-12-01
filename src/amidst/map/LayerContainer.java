package amidst.map;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.loader.FragmentLoader;
import amidst.map.layer.LayerType;

public class LayerContainer {
	private final List<AtomicBoolean> invalidatedLayers;
	private final List<LayerDeclaration> declarations;
	private final List<FragmentConstructor> constructors;
	private final List<FragmentLoader> loaders;
	private final List<FragmentDrawer> drawers;

	public LayerContainer(List<AtomicBoolean> invalidatedLayers,
			List<LayerDeclaration> declarations,
			List<FragmentConstructor> constructors,
			List<FragmentLoader> loaders, List<FragmentDrawer> drawers) {
		this.invalidatedLayers = invalidatedLayers;
		this.declarations = declarations;
		this.constructors = constructors;
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

	public void invalidateLayer(LayerType layerType) {
		invalidatedLayers.get(layerType.ordinal()).set(true);
	}

	public void constructAll(Fragment fragment) {
		for (FragmentConstructor constructor : constructors) {
			constructor.construct(fragment);
		}
	}

	public void loadAll(Fragment fragment) {
		for (FragmentLoader loader : loaders) {
			loader.load(fragment);
		}
	}

	public void reloadInvalidated(Fragment fragment) {
		for (int i = 0; i < loaders.size(); i++) {
			if (invalidatedLayers.get(i).get()) {
				loaders.get(i).reload(fragment);
			}
		}
	}
}
