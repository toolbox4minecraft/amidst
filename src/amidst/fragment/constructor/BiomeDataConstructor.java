package amidst.fragment.constructor;

import amidst.map.Fragment;
import amidst.map.LayerDeclaration;
import amidst.minecraft.world.Resolution;

public class BiomeDataConstructor extends ImageConstructor {
	public BiomeDataConstructor(LayerDeclaration declaration,
			Resolution resolution) {
		super(declaration, resolution);
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(size, size);
		super.construct(fragment);
	}
}
