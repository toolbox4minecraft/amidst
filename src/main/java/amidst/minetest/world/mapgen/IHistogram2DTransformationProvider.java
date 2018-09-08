package amidst.minetest.world.mapgen;

public interface IHistogram2DTransformationProvider {
	/**
	 * Returns an adjusted histogram (scaled, translated, etc.)
	 * @param source is the histogram to be transformed
	 * @param argument is used if the transformation requires an argument (such 
	 * as altitude, for a climate histogram).
	 */
	IHistogram2D getTransformedHistogram(IHistogram2D source, float argument);
}
