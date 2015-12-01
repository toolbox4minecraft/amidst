package amidst.map;

import amidst.map.layer.LayerType;
import amidst.preferences.PrefModel;

public class LayerDeclaration {
	private final int layerId;
	private final LayerType layerType;
	private final PrefModel<Boolean> isVisiblePreference;

	public LayerDeclaration(LayerType layerType,
			PrefModel<Boolean> isVisiblePreference) {
		this.layerId = layerType.ordinal();
		this.layerType = layerType;
		this.isVisiblePreference = isVisiblePreference;
	}

	public int getLayerId() {
		return layerId;
	}

	public LayerType getLayerType() {
		return layerType;
	}

	public boolean isVisible() {
		return isVisiblePreference.get();
	}
}
