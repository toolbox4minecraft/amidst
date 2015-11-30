package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.preferences.PrefModel;

public abstract class Layer {
	protected final Map map;
	protected final LayerType layerType;
	protected final PrefModel<Boolean> isVisiblePreference;

	public Layer(Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference) {
		this.map = map;
		this.layerType = layerType;
		this.isVisiblePreference = isVisiblePreference;
	}

	public LayerType getLayerType() {
		return layerType;
	}

	public boolean isVisible() {
		return isVisiblePreference.get();
	}

	public void construct(Fragment fragment) {
	}

	public abstract void load(Fragment fragment);

	public abstract void reload(Fragment fragment);

	public abstract void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix);
}
