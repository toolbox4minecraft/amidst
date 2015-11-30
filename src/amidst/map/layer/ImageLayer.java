package amidst.map.layer;

import amidst.minecraft.world.Resolution;
import amidst.preferences.PrefModel;

public class ImageLayer extends Layer {
	public ImageLayer(LayerType layerType,
			PrefModel<Boolean> isVisiblePreference,
			ColorProvider colorProvider, Resolution resolution) {
		super(layerType, isVisiblePreference, new ImageDrawer(resolution,
				layerType), new ImageConstructor(layerType, resolution),
				new ImageLoader(layerType, colorProvider, resolution));
	}
}
