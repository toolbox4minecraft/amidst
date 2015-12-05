package amidst.fragment.constructor;

import amidst.fragment.Fragment;
import amidst.minecraft.world.Resolution;

public class BiomeDataConstructor implements FragmentConstructor {
	private final int size;

	public BiomeDataConstructor(Resolution resolution) {
		this.size = resolution.getStepsPerFragment();
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(size, size);
	}
}
