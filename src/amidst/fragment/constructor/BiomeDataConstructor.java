package amidst.fragment.constructor;

import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.Resolution;

@NotThreadSafe
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
