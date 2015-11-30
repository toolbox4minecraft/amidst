package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.BiomeDataOracle;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;
import amidst.preferences.PrefModel;

public class BiomeLayer extends ImageLayer {
	private final BiomeDataOracle biomeDataOracle;

	public BiomeLayer(World world, Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference,
			ColorProvider colorProvider, Resolution resolution,
			BiomeDataOracle biomeDataOracle) {
		super(world, map, layerType, isVisiblePreference, colorProvider,
				resolution);
		this.biomeDataOracle = biomeDataOracle;
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(size, size);
		super.construct(fragment);
	}

	@Override
	public void load(Fragment fragment) {
		fragment.populateBiomeData(biomeDataOracle);
		super.load(fragment);
	}
}
