package amidst.mojangapi.world.filter;

import java.lang.IllegalArgumentException;
import java.lang.Comparable;

import amidst.mojangapi.world.World;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.coordinates.Resolution;

public abstract class BaseFilter implements Comparable<BaseFilter> {
	public final long worldFilterSize;
	// this is a quarter of the whole world size in length not area.
	// i.e. a worldFilterSize of 1024 will have a
	// quarterFilterSize of 256 NOT 512 as would be if area
	public final long quarterFilterSize;
	protected final CoordinatesInWorld corner;
	protected String group;
	protected long scoreValue;

	public BaseFilter(long worldFilterDistance) {
		if (worldFilterDistance % Resolution.FRAGMENT.getStep() != 0) {
			// Structure filters check spaces in fragment size, so filter
			// distance not a multiple of
			// fragment size will include more area in the filter than expected
			throw new IllegalArgumentException("World filter size must be a multiple of "
					+ Resolution.FRAGMENT.getStep() + "." + System.lineSeparator());
		}

		this.worldFilterSize = worldFilterDistance * 2;
		this.quarterFilterSize = Resolution.QUARTER.convertFromWorldToThis(this.worldFilterSize);
		this.corner = new CoordinatesInWorld(-this.worldFilterSize / 2, -this.worldFilterSize / 2);
	}

	abstract protected boolean isValid(World world, short[][] region);

	public int compareTo(BaseFilter f) {
		return (int) (this.quarterFilterSize - f.quarterFilterSize);
	}
}
