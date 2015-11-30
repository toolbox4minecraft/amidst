package amidst.fragment.constructor;

import amidst.map.Fragment;
import amidst.map.layer.LayerType;
import amidst.minecraft.world.Resolution;

public class BiomeDataConstructor extends ImageConstructor {
	public BiomeDataConstructor(LayerType layerType, Resolution resolution) {
		super(layerType, resolution);
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(size, size);
		super.construct(fragment);
	}
}
