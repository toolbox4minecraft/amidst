package amidst.utilities;

import amidst.map.Fragment;

public enum CoordinateUtils {
	;

	public static int toFragment(int inWorld) {
		return modulo(inWorld, Fragment.SIZE);
	}

	public static int toWorld(int inWorldOfFragment, int inFragment) {
		return inWorldOfFragment + inFragment;
	}

	public static int toWorldOfFragment(int inWorld) {
		return inWorld - modulo(inWorld, Fragment.SIZE);
	}

	private static int modulo(int a, int b) {
		return getModuloCorrection(a, b) + a % b;
	}

	private static int getModuloCorrection(int a, int b) {
		if (a < 0) {
			return b;
		} else {
			return 0;
		}
	}

	public static void main(String[] args) {
		if (ensureCoordinateConversionWorks()) {
			System.out.println("Coordinate conversion is working!");
		} else {
			System.out.println("Coordinate conversion is faulty!");
		}
	}

	private static boolean ensureCoordinateConversionWorks() {
		boolean successful = true;
		for (int inWorld = -100; inWorld < 100; inWorld++) {
			int inFragment = toFragment(inWorld);
			int inWorldOfFragment = toWorldOfFragment(inWorld);
			int inWorld2 = toWorld(inWorldOfFragment, inFragment);
			if (inWorld != inWorld2) {
				successful = false;
				System.out.println(inWorld + " != " + inWorld2 + " ("
						+ inWorldOfFragment + " + " + inFragment + ")");
			}
		}
		return successful;
	}
}
