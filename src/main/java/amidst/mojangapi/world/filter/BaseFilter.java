package amidst.mojangapi.world.filter;

import java.lang.IllegalArgumentException;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

public abstract class BaseFilter {
	private short[][] evaluatedRegion = null;

	protected final long worldFilterSize;
	protected final long quarterFilterSize;
	protected final CoordinatesInWorld corner;

	public BaseFilter(long worldFilterDistance) {
		if (worldFilterDistance % Resolution.FRAGMENT.getStep() != 0) {
			// Structure filters check spaces in fragment size, so filter
			// distance not a multiple of
			// fragment size will include more area in the filter than expected
			throw new IllegalArgumentException(
					"World filter size must be a multiple of " + Resolution.FRAGMENT.getStep());
		}

		this.worldFilterSize = worldFilterDistance * 2;
		this.quarterFilterSize = Resolution.QUARTER.convertFromWorldToThis(this.worldFilterSize);
		this.corner = new CoordinatesInWorld(-this.worldFilterSize / 2, -this.worldFilterSize / 2);
	}

	public final boolean isValid(World world) {
		return isValid(world, getUpdatedRegion(world));
	}

	abstract protected boolean isValid(World world, short[][] region);

	private short[][] getUpdatedRegion(World world) {
		if (this.evaluatedRegion == null) {
			this.evaluatedRegion = new short[(int) this.quarterFilterSize][(int) this.quarterFilterSize];
		}

		world.getBiomeDataOracle().populateArray(corner, evaluatedRegion, true);
		return evaluatedRegion;
	}
}
