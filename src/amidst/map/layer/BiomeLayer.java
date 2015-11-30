package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.Biome;
import amidst.minecraft.world.BiomeDataProvider;
import amidst.minecraft.world.Resolution;
import amidst.minecraft.world.World;
import amidst.utilities.ColorUtils;

public class BiomeLayer extends ImageLayer {
	public static final LayerType LAYER_TYPE = LayerType.BIOME;
	public static final Resolution RESOLUTION = Resolution.QUARTER;

	public BiomeLayer(World world, Map map) {
		super(world, map, LAYER_TYPE, RESOLUTION);
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(BiomeDataProvider.SIZE, BiomeDataProvider.SIZE);
		super.construct(fragment);
	}

	@Override
	public void load(Fragment fragment, int[] imageCache) {
		world.populateBiomeDataArray(fragment);
		doLoad(fragment, imageCache);
	}

	@Override
	protected int getColorAt(Fragment fragment, long cornerX, long cornerY,
			int x, int y) {
		return getColor(fragment.getBiomeDataAt(x, y));
	}

	protected int getColor(int biome) {
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
