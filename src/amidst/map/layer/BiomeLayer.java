package amidst.map.layer;

import amidst.fragment.colorprovider.ColorProvider;
import amidst.fragment.constructor.BiomeDataConstructor;
import amidst.fragment.drawer.ImageDrawer;
import amidst.fragment.loader.BiomeDataLoader;
import amidst.minecraft.world.BiomeDataOracle;
import amidst.minecraft.world.Resolution;
import amidst.preferences.PrefModel;

public class BiomeLayer extends Layer {
	public BiomeLayer(LayerType layerType,
			PrefModel<Boolean> isVisiblePreference,
			ColorProvider colorProvider, Resolution resolution,
			BiomeDataOracle biomeDataOracle) {
		super(layerType, isVisiblePreference, new ImageDrawer(resolution,
				layerType), new BiomeDataConstructor(layerType, resolution),
				new BiomeDataLoader(layerType, colorProvider, resolution,
						biomeDataOracle));
	}
}
