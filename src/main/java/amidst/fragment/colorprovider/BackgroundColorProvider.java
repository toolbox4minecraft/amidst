package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.gui.main.viewer.DimensionSelection;
import amidst.logging.Log;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.BiomeColor;

@ThreadSafe
public class BackgroundColorProvider implements ColorProvider {
	private final BiomeColorProvider biomeColorProvider;
	private final TheEndColorProvider theEndColorProvider;
	private final DimensionSelection dimensionSelection;

	public BackgroundColorProvider(BiomeColorProvider biomeColorProvider,
			TheEndColorProvider theEndColorProvider,
			DimensionSelection dimensionSelection) {
		this.biomeColorProvider = biomeColorProvider;
		this.theEndColorProvider = theEndColorProvider;
		this.dimensionSelection = dimensionSelection;
	}

	@Override
	public int getColorAt(Fragment fragment, long cornerX, long cornerY, int x,
			int y) {
		if (dimensionSelection.isDimension(Dimension.OVERWORLD)) {
			return biomeColorProvider.getColorAt(fragment, cornerX, cornerY, x,
					y);
		} else if (dimensionSelection.isDimension(Dimension.END)) {
			return theEndColorProvider.getColorAt(fragment, cornerX, cornerY,
					x, y);
		} else {
			Log.w("unsupported dimension");
			return BiomeColor.unknown().getRGB();
		}
	}
}
