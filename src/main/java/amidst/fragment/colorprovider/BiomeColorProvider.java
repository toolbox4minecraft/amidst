package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@ThreadSafe
public class BiomeColorProvider implements ColorProvider {
	private final BiomeSelection biomeSelection;
	private final BiomeProfileSelection biomeProfileSelection;

	public BiomeColorProvider(BiomeSelection biomeSelection, BiomeProfileSelection biomeProfileSelection) {
		this.biomeSelection = biomeSelection;
		this.biomeProfileSelection = biomeProfileSelection;
	}

	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		return getColor(fragment.getBiomeDataAt(x, y));
	}

	private int getColor(int biomeIndex) {
		if (biomeSelection.isVisible(biomeIndex)) {
			return getBiomeColor(biomeIndex).getRGB();
		} else {
			return getBiomeColor(biomeIndex).getHiddenRGB();
		}
	}

	private BiomeColor getBiomeColor(int biomeIndex) {
		return biomeProfileSelection.getBiomeColorOrUnknown(biomeIndex);
	}
}
