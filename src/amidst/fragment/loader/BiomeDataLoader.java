package amidst.fragment.loader;

import amidst.fragment.colorprovider.ColorProvider;
import amidst.fragment.layer.LayerDeclaration;
import amidst.map.Fragment;
import amidst.minecraft.world.BiomeDataOracle;
import amidst.minecraft.world.Resolution;

public class BiomeDataLoader extends ImageLoader {
	private final BiomeDataOracle biomeDataOracle;

	public BiomeDataLoader(LayerDeclaration declaration, Resolution resolution,
			ColorProvider colorProvider, BiomeDataOracle biomeDataOracle) {
		super(declaration, resolution, colorProvider);
		this.biomeDataOracle = biomeDataOracle;
	}

	@Override
	public void load(Fragment fragment) {
		fragment.populateBiomeData(biomeDataOracle);
		super.load(fragment);
	}
}
