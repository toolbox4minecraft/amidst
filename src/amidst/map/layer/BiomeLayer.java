package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;
import amidst.preferences.PrefModel;

public class BiomeLayer extends ImageLayer {
	public BiomeLayer(World world, Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference) {
		super(world, map, layerType, isVisiblePreference,
				new BiomeColorProvider(map), Resolution.QUARTER);
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(size, size);
		super.construct(fragment);
	}

	@Override
	public void load(Fragment fragment) {
		fragment.populateBiomeData(world.getBiomeDataOracle());
		super.load(fragment);
	}
}
