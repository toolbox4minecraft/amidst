package amidst.mojangapi.world.oracle.end;

import amidst.documentation.Immutable;

// TODO: check sign of differences ... (this.chunkX - chunkX) vs (chunkX - this.chunkX)
@Immutable
public class LargeEndIsland {
	private static final short X_ADJUSTMENT = 1;
	private static final short Y_ADJUSTMENT = 1;

	private final long chunkX;
	private final long chunkY;
	private final float erosionFactor;

	protected LargeEndIsland(long chunkX, long chunkY, float erosionFactor) {
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.erosionFactor = erosionFactor;
	}

	/**
	 * Retuns a value between 80 and -100 which indicates this island's
	 * influence at the block coordindates given. A non-negative value indicates
	 * there will be solid ground, while a negative value indicates the
	 * rocky-island-shore, which might be solid ground (but that becomes less
	 * likely the lower the value).
	 */
	public float influenceAtBlock(long x, long y) {
		// Add 8 blocks to both axis because all the Minecraft calculations are
		// done using chunk coordinates and are converted as being the center of
		// the chunk whenever translated to block coordinates, whereas Amidst
		// treats chunk coords as blockCoordinates >> 4.
		// This function also does a floating point divide by 16 instead of
		// shifting by 4 in order to maintain sub-chunk accuracy with x & y.
		float chunkX = (x + 8) / 16.0f;
		float chunkY = (y + 8) / 16.0f;
		float adjustedX = (this.chunkX - chunkX) * 2 + X_ADJUSTMENT;
		float adjustedY = (this.chunkY - chunkY) * 2 + Y_ADJUSTMENT;
		return getResult(adjustedX * adjustedX + adjustedY * adjustedY);
	}

	/**
	 * A version of influenceAt() that more exactly adheres to Minecraft's
	 * algorithm, for use in testing for End Cities.
	 */
	public float influenceAtChunk(long chunkX, long chunkY) {
		long adjustedX = (chunkX - this.chunkX) * 2 + X_ADJUSTMENT;
		long adjustedY = (chunkY - this.chunkY) * 2 + Y_ADJUSTMENT;
		return getResult(adjustedX * adjustedX + adjustedY * adjustedY);
	}

	private float getResult(double squared) {
		float result = 100.0f - (float) Math.sqrt(squared) * erosionFactor;
		if (result > 80.0f) {
			return 80.0f;
		} else if (result < -100.0f) {
			return -100.0f;
		} else {
			return result;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + chunkX);
		result = (int) (prime * result + chunkY);
		result = prime * result + Float.floatToIntBits(erosionFactor);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LargeEndIsland)) {
			return false;
		}
		LargeEndIsland other = (LargeEndIsland) obj;
		if (chunkX != other.chunkX) {
			return false;
		}
		if (chunkY != other.chunkY) {
			return false;
		}
		if (Float.floatToIntBits(erosionFactor) != Float.floatToIntBits(other.erosionFactor)) {
			return false;
		}
		return true;
	}
}
