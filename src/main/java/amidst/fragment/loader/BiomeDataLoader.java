package amidst.fragment.loader;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.IBiomeDataOracle;
import amidst.fragment.layer.LayerDeclaration;
import amidst.mojangapi.world.Dimension;

@NotThreadSafe
public class BiomeDataLoader extends FragmentLoader {
	private final IBiomeDataOracle biomeDataOracle;

	public BiomeDataLoader(LayerDeclaration declaration, IBiomeDataOracle biomeDataOracle) {
		super(declaration);
		this.biomeDataOracle = biomeDataOracle;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void load(Dimension dimension, Fragment fragment) {
		doLoad(fragment);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public void reload(Dimension dimension, Fragment fragment) {
		doLoad(fragment);
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private void doLoad(Fragment fragment) {
		fragment.populateBiomeData(biomeDataOracle);
	}
}
