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

	int last_x = Integer.MIN_VALUE;
	int last_biomeIndex = Integer.MIN_VALUE;
	int last_color = 0;
	
	public BiomeColorProvider(BiomeSelection biomeSelection, BiomeProfileSelection biomeProfileSelection) {
		this.biomeSelection = biomeSelection;
		this.biomeProfileSelection = biomeProfileSelection;
	}

	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {

		// This is just an optimized form of:  return getColor(fragment.getBiomeIndexAt(x, y));		
		int biomeIndex = fragment.getBiomeIndexAt(x, y);
		if (biomeIndex != last_biomeIndex || x != (last_x + 1)) {
			// Don't skip the color lookup
			last_biomeIndex = biomeIndex;			
			last_color = getColor(biomeIndex);
		}
		last_x = x;			
		return last_color;		
	}

	private int getColor(int biomeIndex) {
		if (biomeSelection.isSelected(biomeIndex)) {
			return getBiomeColor(biomeIndex).getRGB();
		} else {
			return getBiomeColor(biomeIndex).getDeselectRGB();
		}
	}

	private BiomeColor getBiomeColor(int biomeIndex) {
		return biomeProfileSelection.getBiomeColorOrUnknown(biomeIndex);
	}
}
