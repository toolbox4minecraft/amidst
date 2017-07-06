package amidst.mojangapi.world.test;

import org.junit.Assert;
import org.junit.Test;

import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.mojangapi.world.coordinates.Region;

public class TestRegion {

	@Test
	public void testRegionAppartenance() {
		Region.Box box = Region.box(-10, -20, 30, 40);
		Assert.assertTrue(box.contains(box));
		Assert.assertTrue(box.contains(0, 0));
		Assert.assertTrue(box.contains(box.getXMin(), box.getYMin()));
		Assert.assertFalse(box.contains(box.getXMin(), box.getYMax()));
		Assert.assertFalse(box.contains(box.getXMax(), box.getYMin()));
		Assert.assertFalse(box.contains(box.getXMax(), box.getYMax()));
		
		Region.Circle circle = Region.circle(-10, -20, 50);
		Assert.assertTrue(circle.contains(circle));
		Assert.assertTrue(circle.contains(0, 0));
		Coordinates center = circle.getCenter();
		Assert.assertTrue(circle.contains(center.add(0, 50)));
		Assert.assertTrue(circle.contains(center.add(0, -50)));
		Assert.assertTrue(circle.contains(center.add(50, 0)));
		Assert.assertTrue(circle.contains(center.add(-50, 0)));
		Assert.assertFalse(circle.contains(150, 20));
	}

	@Test
	public void testBoxBoxIntersection() {
		Region.Box box = Region.box(0, 0, 50, 50);
		Assert.assertTrue(box.intersectsWith(box));

		Assert.assertTrue(box.intersectsWith(Region.box(20, 10, 100, 100)));
		Assert.assertFalse(box.intersectsWith(Region.box(-50, -70, 30, 30)));
	}

	@Test
	public void testCircleCircleIntersection() {
		Region.Circle circle = Region.circle(0, 0, 100);
		Assert.assertTrue(circle.intersectsWith(circle));
		
		Assert.assertTrue(circle.intersectsWith(Region.circle(50, 50, 50)));
		Assert.assertTrue(circle.intersectsWith(Region.circle(100, 90, 50)));
		
		Assert.assertFalse(circle.intersectsWith(Region.circle(120, -100, 10)));
	}
	
	@Test
	public void testBoxCircleIntersection() {
		Region.Box box = Region.box(0, 0, 200, 100);

		// Inside inner radius
		Assert.assertTrue(box.intersectsWith(Region.circle(0, 0, 100)));

		// Outside outer radius
		Assert.assertFalse(box.intersectsWith(Region.circle(500, 500, 100)));

		// Intermediate
		Assert.assertTrue(box.intersectsWith(Region.circle(-10, -10, 50)));
		Assert.assertFalse(box.intersectsWith(Region.circle(100, -50, 40)));
		
		// Other
		Assert.assertTrue(Region.box(-1536, 0, 512, 512).intersectsWith(Region.circle(-140, 256, 1024)));
	}
}
