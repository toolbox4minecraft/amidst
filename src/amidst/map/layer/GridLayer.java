package amidst.map.layer;

import amidst.map.Map;
import amidst.preferences.PrefModel;

public class GridLayer extends Layer {
	public GridLayer(LayerType layerType,
			PrefModel<Boolean> isVisiblePreference, Map map) {
		super(layerType, isVisiblePreference, new GridDrawer(map),
				new DummyConstructor(), new DummyLoader());
	}
}
