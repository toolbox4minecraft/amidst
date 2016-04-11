package amidst.mojangapi.world.coordinates;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;

@Immutable
public enum CoordinateUtils {
	;

	public static boolean isInBounds(long x, long y, long offsetX, long offsetY, long width, long height) {
		return x >= offsetX && x < offsetX + width && y >= offsetY && y < offsetY + height;
	}

	/**
	 * @return world coordinates relative to its fragment corner (corrected
	 *         modulo)
	 */
	public static long toFragmentRelative(long inWorld) {
		return modulo(inWorld, Fragment.SIZE);
	}

	/**
	 * @return world coordinates (addition)
	 */
	public static long toWorld(long inWorldOfFragment, long inFragment) {
		return inWorldOfFragment + inFragment;
	}

	/**
	 * @return world coordinates of the corner of the given coordinates fragment
	 */
	public static long toFragmentCorner(long inWorld) {
		return inWorld - modulo(inWorld, Fragment.SIZE);
	}

	private static long modulo(long a, long b) {
		return ((a % b) + b) % b;
	}
}
