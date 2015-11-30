package amidst.map.layer;

import amidst.fragment.constructor.FragmentConstructor;
import amidst.fragment.drawer.FragmentDrawer;
import amidst.fragment.loader.FragmentLoader;
import amidst.preferences.PrefModel;

public class Layer {
	private final LayerType layerType;
	private final PrefModel<Boolean> isVisiblePreference;
	private final FragmentDrawer drawer;
	private final FragmentConstructor constructor;
	private final FragmentLoader loader;

	public Layer(LayerType layerType, PrefModel<Boolean> isVisiblePreference,
			FragmentDrawer drawer, FragmentConstructor constructor,
			FragmentLoader loader) {
		this.layerType = layerType;
		this.isVisiblePreference = isVisiblePreference;
		this.drawer = drawer;
		this.constructor = constructor;
		this.loader = loader;
	}

	public LayerType getLayerType() {
		return layerType;
	}

	public boolean isVisible() {
		return isVisiblePreference.get();
	}

	public FragmentDrawer getFragmentDrawer() {
		return drawer;
	}

	public FragmentConstructor getFragmentConstructor() {
		return constructor;
	}

	public FragmentLoader getFragmentLoader() {
		return loader;
	}
}
