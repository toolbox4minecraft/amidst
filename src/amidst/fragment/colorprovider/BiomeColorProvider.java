package amidst.fragment.colorprovider;

import amidst.map.BiomeSelection;
import amidst.map.Fragment;
import amidst.minecraft.Biome;
import amidst.utilities.ColorUtils;

public class BiomeColorProvider implements ColorProvider {
	private final BiomeSelection biomeSelection;

	public BiomeColorProvider(BiomeSelection biomeSelection) {
		this.biomeSelection = biomeSelection;
	}

	@Override
	public int getColorAt(Fragment fragment, long cornerX, long cornerY, int x,
			int y) {
		return getColor(fragment.getBiomeDataAt(x, y));
	}

	private int getColor(int biome) {
		if (!biomeSelection.isSelected(biome)) {
			return ColorUtils.deselectColor(doGetColor(biome));
		} else {
			return doGetColor(biome);
		}
	}

	private int doGetColor(int biome) {
		return Biome.getByIndex(biome).getColor();
	}
}
