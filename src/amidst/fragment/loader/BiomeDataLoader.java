package amidst.fragment.loader;

import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.minecraft.world.BiomeDataOracle;

public class BiomeDataLoader extends FragmentLoader {
	private final BiomeDataOracle biomeDataOracle;

	public BiomeDataLoader(LayerDeclaration declaration,
			BiomeDataOracle biomeDataOracle) {
		super(declaration);
		this.biomeDataOracle = biomeDataOracle;
	}

	@Override
	public void load(Fragment fragment) {
		fragment.populateBiomeData(biomeDataOracle);
	}

	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
