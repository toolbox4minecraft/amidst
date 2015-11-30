package amidst.map;

import amidst.minecraft.world.BiomeDataProvider;

public class FragmentFactory {
	public Fragment create() {
		return new Fragment(createBiomeData());
	}

	private short[][] createBiomeData() {
		return BiomeDataProvider.createEmptyBiomeDataArray();
	}
}
