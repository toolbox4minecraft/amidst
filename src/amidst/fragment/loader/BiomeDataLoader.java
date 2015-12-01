package amidst.fragment.loader;

import amidst.fragment.colorprovider.ColorProvider;
import amidst.map.Fragment;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.BiomeDataOracle;
import amidst.minecraft.world.Resolution;

public class BiomeDataLoader extends ImageLoader {
	private final BiomeDataOracle biomeDataOracle;

	public BiomeDataLoader(LayerType layerType, Resolution resolution,
			ColorProvider colorProvider, BiomeDataOracle biomeDataOracle) {
		super(layerType, resolution, colorProvider);
		this.biomeDataOracle = biomeDataOracle;
	}

	@Override
	public void load(Fragment fragment) {
		fragment.populateBiomeData(biomeDataOracle);
		super.load(fragment);
	}

	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
