package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.Biome;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;
import amidst.preferences.PrefModel;
import amidst.utilities.ColorUtils;

public class BiomeLayer extends ImageLayer {
	public BiomeLayer(World world, Map map, LayerType layerType,
			PrefModel<Boolean> isVisiblePreference) {
		super(world, map, layerType, isVisiblePreference, Resolution.QUARTER);
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(size, size);
		super.construct(fragment);
	}

	@Override
	public void load(Fragment fragment) {
		fragment.populateBiomeData(world.getBiomeDataOracle());
		super.load(fragment);
	}

	@Override
	protected int getColorAt(Fragment fragment, long cornerX, long cornerY,
			int x, int y) {
		return getColor(fragment.getBiomeDataAt(x, y));
	}

	private int getColor(int biome) {
		if (!map.getBiomeSelection().isSelected(biome)) {
			return ColorUtils.deselectColor(doGetColor(biome));
		} else {
			return doGetColor(biome);
		}
	}

	private int doGetColor(int biome) {
		return Biome.getByIndex(biome).getColor();
	}
}
