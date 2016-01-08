package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.dimension.DimensionIds;
import amidst.gui.main.viewer.DimensionSelection;

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
		if (dimensionSelection.isDimensionId(DimensionIds.THE_END)) {
			return theEndColorProvider.getColorAt(fragment, cornerX, cornerY,
					x, y);
		} else {
			return biomeColorProvider.getColorAt(fragment, cornerX, cornerY, x,
					y);
		}
	}
}
