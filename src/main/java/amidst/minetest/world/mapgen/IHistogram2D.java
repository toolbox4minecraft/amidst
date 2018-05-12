package amidst.minetest.world.mapgen;

import javax.vecmath.Point2d;

public interface IHistogram2D {
	/**
	 * Returns a value between 0 and 1 which represents how frequently the
	 * samples fall into the bucket at (x, y).
	 * It's up to the implementation to decide what size its buckets are, and
	 * whether x and y get rounded to the nearest bucket, or bucket values are
	 * interpolated.
	 */
	double frequencyOfOccurance(float x, float y);

	/**
	 * Returns the "FrequencyOfOccurance" value at which 'percentile' amount of
	 * samples will fall beneath.
	 * So if percentile was 10, then a value between 0 and 1 would be returned such
	 * that 10% of results from FrequencyOfOccurance() would fall below it.
	 */
	double frequencyAtPercentile(double percentile);

	/**
	 * get's the mean value of the distribution.
	 * If the histogram were a normal distribution, this would return the location of
	 * the peak. If  the histogram had two peaks, it would return a location between them.
	 */
	Point2d getSampleMean();
}
