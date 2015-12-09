package amidst.fragment.colorprovider;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.fragment.Fragment;
import amidst.gui.worldsurroundings.BiomeSelection;
import amidst.preferences.BiomeColorProfileSelection;
import amidst.utilities.ColorUtils;

public class BiomeColorProvider implements ColorProvider {
	private final BiomeSelection biomeSelection;
	private final BiomeColorProfileSelection biomeColorProfileSelection;

	public BiomeColorProvider(BiomeSelection biomeSelection,
			BiomeColorProfileSelection biomeColorProfileSelection) {
		this.biomeSelection = biomeSelection;
		this.biomeColorProfileSelection = biomeColorProfileSelection;
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	@Override
	public int getColorAt(Fragment fragment, long cornerX, long cornerY, int x,
			int y) {
		return getColor(fragment.getBiomeDataAt(x, y));
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private int getColor(int biome) {
		if (!biomeSelection.isSelected(biome)) {
			return ColorUtils.deselectColor(doGetColor(biome));
		} else {
			return doGetColor(biome);
		}
	}

	@CalledOnlyBy(AmidstThread.FRAGMENT_LOADER)
	private int doGetColor(int biome) {
		return biomeColorProfileSelection.getColorIntByBiomeIndex(biome);
	}
}
