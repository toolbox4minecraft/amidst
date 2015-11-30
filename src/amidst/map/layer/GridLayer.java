package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.preferences.PrefModel;

public class GridLayer extends Layer {
	public GridLayer(Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference) {
		super(map, layerType, isVisiblePreference, new GridDrawer(map));
	}

	@Override
	public void load(Fragment fragment) {
		// noop
	}

	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
