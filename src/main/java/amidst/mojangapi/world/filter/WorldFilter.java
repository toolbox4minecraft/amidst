package amidst.mojangapi.world.filter;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;
import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

@Immutable
public abstract class WorldFilter {
	protected final long worldFilterSize;
	protected final long quarterFilterSize;
	protected final CoordinatesInWorld corner;

	public WorldFilter(long worldFilterSize) {
		ensureIsMultipleOfFragmentSize(worldFilterSize);
		this.worldFilterSize = worldFilterSize;
		this.quarterFilterSize = Resolution.QUARTER.convertFromWorldToThis(this.worldFilterSize);
		this.corner = new CoordinatesInWorld(-this.worldFilterSize, -this.worldFilterSize);
	}

	/**
	 * Structure filters check spaces in fragment size, so filter distance not a
	 * multiple of fragment size will include more area in the filter than
	 * expected
	 */
	private void ensureIsMultipleOfFragmentSize(long worldFilterSize) {
		if (worldFilterSize % Fragment.SIZE != 0) {
			throw new IllegalArgumentException("World filter size must be a multiple of " + Fragment.SIZE);
		}
	}

	public abstract boolean isValid(World world);
}
