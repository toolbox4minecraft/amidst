package amidst.fragment.colorprovider;

import amidst.documentation.ThreadSafe;
import amidst.fragment.Fragment;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.biome.BiomeColor;

@ThreadSafe
public class BackgroundColorProvider implements ColorProvider {
	private final BiomeColorProvider biomeColorProvider;
	private final TheEndColorProvider theEndColorProvider;

	public BackgroundColorProvider(BiomeColorProvider biomeColorProvider, TheEndColorProvider theEndColorProvider) {
		this.biomeColorProvider = biomeColorProvider;
		this.theEndColorProvider = theEndColorProvider;
	}

	@Override
	public int getColorAt(Dimension dimension, Fragment fragment, long cornerX, long cornerY, int x, int y) {
		if (dimension.equals(Dimension.OVERWORLD)) {
			return biomeColorProvider.getColorAt(dimension, fragment, cornerX, cornerY, x, y);
		} else if (dimension.equals(Dimension.END)) {
			return theEndColorProvider.getColorAt(dimension, fragment, cornerX, cornerY, x, y);
		} else {
			AmidstLogger.warn("unsupported dimension");
			return BiomeColor.unknown().getRGB();
		}
	}
}
