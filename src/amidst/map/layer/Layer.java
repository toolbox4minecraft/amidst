package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.preferences.PrefModel;

public abstract class Layer {
	protected final Map map;
	protected final LayerType layerType;
	private final PrefModel<Boolean> isVisiblePreference;
	private final FragmentDrawer drawer;

	public Layer(Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference, FragmentDrawer drawer) {
		this.map = map;
		this.layerType = layerType;
		this.isVisiblePreference = isVisiblePreference;
		this.drawer = drawer;
	}

	public LayerType getLayerType() {
		return layerType;
	}

	public FragmentDrawer getFragmentDrawer() {
		return drawer;
	}

	public boolean isVisible() {
		return isVisiblePreference.get();
	}

	public void construct(Fragment fragment) {
	}

	public abstract void load(Fragment fragment);

	public abstract void reload(Fragment fragment);
}
