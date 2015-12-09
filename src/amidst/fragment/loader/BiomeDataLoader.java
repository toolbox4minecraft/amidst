package amidst.fragment.loader;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.oracle.BiomeDataOracle;

public class BiomeDataLoader extends FragmentLoader {
	private final BiomeDataOracle biomeDataOracle;

	public BiomeDataLoader(LayerDeclaration declaration,
			BiomeDataOracle biomeDataOracle) {
		super(declaration);
		this.biomeDataOracle = biomeDataOracle;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void load(Fragment fragment) {
		fragment.populateBiomeData(biomeDataOracle);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void reload(Fragment fragment) {
		// noop
	}
}
