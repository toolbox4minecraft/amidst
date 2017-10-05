package amidst.fragment.constructor;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.coordinates.Resolution;
import amidst.settings.Setting;

@Immutable
public class BiomeDataConstructor implements FragmentConstructor {
	private final int size;
	private final Resolution resolution;

	@CalledOnlyBy(AmidstThread.EDT)
	public BiomeDataConstructor(Resolution resolution) {
		this.size = resolution.getStepsPerFragment();
		this.resolution = resolution;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(size, size, resolution);
	}
}
