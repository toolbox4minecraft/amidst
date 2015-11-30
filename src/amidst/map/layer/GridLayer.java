package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.preferences.PrefModel;

public class GridLayer extends Layer {
	public GridLayer(Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference) {
		super(map, layerType, isVisiblePreference);
	}

	@Override
	public void load(Fragment fragment) {
		// noop
	}

	@Override
	public void reload(Fragment fragment) {
		// noop
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
		new GridDrawer(map).draw(fragment, g2d, layerMatrix);
	}
}
