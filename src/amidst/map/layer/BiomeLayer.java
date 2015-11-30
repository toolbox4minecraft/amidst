package amidst.map.layer;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.Biome;
import amidst.minecraft.world.BiomeDataProvider;
import amidst.minecraft.world.World;
import amidst.utilities.ColorUtils;

public class BiomeLayer extends ImageLayer {
	private int biomeSize;

	public BiomeLayer(World world, Map map) {
		super(world, map, LayerType.BIOME, BiomeDataProvider.RESOLUTION);
		this.biomeSize = (int) resolution.convertFromWorldToThis(Fragment.SIZE);
	}

	@Override
	public void construct(Fragment fragment) {
		fragment.initBiomeData(biomeSize, biomeSize);
		super.construct(fragment);
	}

	@Override
	public void load(Fragment fragment) {
		world.populateBiomeDataArray(fragment);
		super.load(fragment);
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
