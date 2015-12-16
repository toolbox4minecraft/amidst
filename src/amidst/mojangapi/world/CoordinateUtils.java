package amidst.mojangapi.world;

import amidst.documentation.Immutable;
import amidst.fragment.Fragment;

@Immutable
public enum CoordinateUtils {
	;

	public static boolean isInBounds(long x, long y, long offsetX,
			long offsetY, long width, long height) {
		return x >= offsetX && x < offsetX + width && y >= offsetY
				&& y < offsetY + height;
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

	// TODO: convert to unit test
	public static void main(String[] args) {
		if (ensureCoordinateConversionWorks()) {
			System.out.println("Coordinate conversion is working!");
		} else {
			System.out.println("Coordinate conversion is faulty!");
		}
	}

	private static boolean ensureCoordinateConversionWorks() {
		boolean successful = true;
		for (long inWorld = -1000; inWorld < 1000; inWorld++) {
			long inFragment = toFragmentRelative(inWorld);
			long inWorldOfFragment = toFragmentCorner(inWorld);
			long inWorld2 = toWorld(inWorldOfFragment, inFragment);
			if (inWorld != inWorld2) {
				successful = false;
				System.out.println(inWorld + " != " + inWorld2 + " ("
						+ inWorldOfFragment + " + " + inFragment + ")");
			}
		}
		return successful;
	}
}
