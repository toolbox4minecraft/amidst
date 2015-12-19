package amidst.mojangapi.world.coordinates;

import org.junit.Assert;
import org.junit.Test;

public class CoordinateUtilsTest {
	@Test
	public void testCoordinateConversion() {
		Assert.assertTrue(ensureCoordinateConversionWorks());
	}

	private static boolean ensureCoordinateConversionWorks() {
		for (long inWorld = -1000; inWorld < 1000; inWorld++) {
			long inFragment = CoordinateUtils.toFragmentRelative(inWorld);
			long inWorldOfFragment = CoordinateUtils.toFragmentCorner(inWorld);
			long actualInWorld = CoordinateUtils.toWorld(inWorldOfFragment,
					inFragment);
			if (inWorld != actualInWorld) {
				return false;
			}
		}
		return true;
	}
}
