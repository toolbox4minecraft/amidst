package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.gui.main.worldsurroundings.BiomeSelection;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.settings.biomecolorprofile.BiomeColorProfileSelection;

@ThreadSafe
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

	private int getColor(int biomeIndex) {
		if (biomeSelection.isSelected(biomeIndex)) {
			return getBiomeColor(biomeIndex).getRGB();
		} else {
			return getBiomeColor(biomeIndex).getDeselectRGB();
		}
	}

	private BiomeColor getBiomeColor(int biomeIndex) {
		return biomeColorProfileSelection.getBiomeColorOrUnknown(biomeIndex);
	}
}
