package amidst.map.layer;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.loader.FragmentLoader;
import amidst.preferences.PrefModel;

public class Layer {
	private final LayerType layerType;
	private final PrefModel<Boolean> isVisiblePreference;
	private final FragmentConstructor constructor;
	private final FragmentLoader loader;
	private final FragmentDrawer drawer;

	public Layer(LayerType layerType, PrefModel<Boolean> isVisiblePreference,
			FragmentConstructor constructor, FragmentLoader loader,
			FragmentDrawer drawer) {
		this.layerType = layerType;
		this.isVisiblePreference = isVisiblePreference;
		this.constructor = constructor;
		this.loader = loader;
		this.drawer = drawer;
	}

	public LayerType getLayerType() {
		return layerType;
	}

	public boolean isVisible() {
		return isVisiblePreference.get();
	}

	public PrefModel<Boolean> getIsVisiblePreference() {
		return isVisiblePreference;
	}

	public FragmentConstructor getFragmentConstructor() {
		return constructor;
	}

	public FragmentLoader getFragmentLoader() {
		return loader;
	}

	public FragmentDrawer getFragmentDrawer() {
		return drawer;
	}
}
