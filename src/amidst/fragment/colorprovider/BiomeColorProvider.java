package amidst.fragment.colorprovider;

import amidst.map.BiomeColorProfileSelection;
import amidst.map.BiomeSelection;
import amidst.map.Fragment;
import amidst.utilities.ColorUtils;

public class BiomeColorProvider implements ColorProvider {
	private final BiomeSelection biomeSelection;
	private final BiomeColorProfileSelection biomeColorProfileSelection;

	public BiomeColorProvider(BiomeSelection biomeSelection,
			BiomeColorProfileSelection biomeColorProfileSelection) {
		this.biomeSelection = biomeSelection;
		this.biomeColorProfileSelection = biomeColorProfileSelection;
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
		return biomeColorProfileSelection.getColorIntByBiomeIndex(biome);
	}
}
