package amidst.mojangapi.world.coordinates;

import amidst.documentation.Immutable;

@Immutable
public abstract class Region {

	private Region() {}
	
	public abstract long getCenterX();
	public abstract long getCenterY();
	public CoordinatesInWorld getCenter() {
		return CoordinatesInWorld.from(getCenterX(), getCenterY());
	}
	public abstract CoordinatesInWorld getCorner();
	public abstract long getWidth();
	public abstract long getHeight();
	
	public abstract boolean contains(long x, long y);
	public abstract boolean contains(CoordinatesInWorld point);
	
	public abstract boolean intersectsWith(Region other);
	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();
	
	public static Box box(long x, long y, long w, long h) {
		if (w <= 0 || h <= 0)
			throw new IllegalArgumentException("width and height must be greater than 0");
		return new Box(x, y, x + w, y + h);
	}
	
	public static Box box(CoordinatesInWorld corner, long w, long h) {
		return box(corner.getX(), corner.getY(), w, h);
	}
	
	public static Box box(CoordinatesInWorld corner1, CoordinatesInWorld corner2) {
		long xMin = Math.min(corner1.getX(), corner2.getX());
		long xMax = Math.max(corner1.getX(), corner2.getX());
		long yMin = Math.min(corner1.getY(), corner2.getY());
		long yMax = Math.max(corner1.getY(), corner2.getY());

		return new Box(xMin, yMin, xMax, yMax);
	}
	
	public static Circle circle(CoordinatesInWorld center, long radius) {
		return circle(center.getX(), center.getY(), radius);
	}
	
	public static Circle circle(long x, long y, long radius) {
		if(radius < 0)
			throw new IllegalArgumentException("radius must be greater than 0");
		return new Circle(x, y, radius);
	}
	
	public static final class Circle extends Region {
		private final long radius;
		private final long radiusSq;
		private final long x;
		private final long y;
		
		private Circle(long x, long y, long r) {
			radius = r;
			radiusSq = r*r;
			this.x = x;
			this.y = y;
		}
		
		@Override
		public long getCenterX() {
			return x;
		}
		
		@Override
		public long getCenterY() {
			return y;
		}
		
		@Override
		public CoordinatesInWorld getCorner() {
			return CoordinatesInWorld.from(x - radius, y - radius);
		}
		
		@Override
		public long getWidth() {
			return radius*2;
		}
		@Override
		public long getHeight() {
			return radius*2;
		}
		
		public long getRadius() {
			return radius;
		}
		
		@Override
		public boolean contains(long x, long y) {
			return distanceSq(this.x, x, this.y, y) <= radiusSq;
		}
		@Override
		public boolean contains(CoordinatesInWorld point) {
			return contains(point.getX(), point.getY());
		}
		@Override
		public boolean intersectsWith(Region other) {
			if(other instanceof Box)
				return intersectsBoxCircle((Box) other, this);
			if(other instanceof Circle)
				return intersectsCircleCircle(this, (Circle) other);
			
			throw new IllegalArgumentException("unknown region type " + other.getClass().getCanonicalName());
		}
		

		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;

			if (other == null)
				return false;

			if (!(other instanceof Circle))
				return false;
			
			Circle o = (Circle) other;
			
			return radius == o.radius && x == o.x && y == o.y;
		}
		@Override
		public int hashCode() {
			int result = 21;
			result = 31*result + (int) ((x ^ (x >>> 32)));
			result = 31*result + (int) ((y ^ (y >>> 32)));
			result = 31*result + (int) ((radius ^ (radius >>> 32)));
			return result;
		}
		@Override
		public String toString() {
			return "[center=[" + x + ", " + y + "],radius=" + radius + "]";
		}
		
		
		private static long distanceSq(long x1, long x2, long y1, long y2) {
			long dx = x1-x2;
			long dy = y1-y2;
			return dx*dx+dy*dy;
		}
	}
	
	public static final class Box extends Region {
		private final long xMin;
		private final long yMin;
		private final long xMax;
		private final long yMax;

		private Box(long xMin, long yMin, long xMax, long yMax) {
			this.xMin = xMin;
			this.yMin = yMin;
			this.xMax = xMax;
			this.yMax = yMax;
		}
		
		@Override
		public long getCenterX() {
			return xMin+xMax/2;
		}
		
		@Override
		public long getCenterY() {
			return yMin+yMax/2;
		}

		@Override
		public long getWidth() {
			return xMax - xMin;
		}

		@Override
		public long getHeight() {
			return yMax - yMin;
		}

		@Override
		public boolean contains(long posX, long posY) {
			return posX >= getXMin() && posX < getXMax() && posY >= getYMin() && posY < getYMax();
		}

		@Override
		public boolean contains(CoordinatesInWorld pos) {
			return contains(pos.getX(), pos.getY());
		}

		@Override
		public boolean intersectsWith(Region other) {
			if(other instanceof Box)
				return intersectsBoxBox(this, (Box) other);
			if(other instanceof Circle)
				return intersectsBoxCircle(this, (Circle) other);
			
			throw new IllegalArgumentException("unknown region type " + other.getClass().getCanonicalName());
		}


		public long getXMin() {
			return xMin;
		}

		public long getYMin() {
			return yMin;
		}

		public long getXMax() {
			return xMax;
		}

		public long getYMax() {
			return yMax;
		}
		
		@Override
		public CoordinatesInWorld getCorner() {
			return CoordinatesInWorld.from(xMin, yMin);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 13;
			result = prime * result + (int) (xMin ^ (xMin >>> 32));
			result = prime * result + (int) (yMin ^ (yMin >>> 32));
			result = prime * result + (int) (xMax ^ (xMax >>> 32));
			result = prime * result + (int) (yMax ^ (yMax >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;

			if (other == null)
				return false;

			if (!(other instanceof Box))
				return false;

			Box o = (Box) other;

			return xMin == o.xMin && yMin == o.yMin && xMax == o.xMax && yMax == o.yMax;
		}

		@Override
		public String toString() {
			return "[min=[" + xMin + ", " + yMin + "], max=[" + xMax + ", " + yMax + "]]";
		}
	}
	
	
	private static boolean intersectsBoxBox(Box r1, Box r2) {
		if (r1.getXMax() <= r2.getXMin()) // this is left of other
			return false;
		if (r1.getYMax() <= r2.getYMin()) // this is below other
			return false;
		if (r1.getXMin() >= r2.getXMax()) // this is right of other
			return false;
		if (r1.getYMin() >= r2.getYMax()) // this is above other
			return false;

		return true;
	}
	
	private static boolean intersectsCircleCircle(Circle r1, Circle r2) {
		// Two circles intersects iff their centers are sufficiently close
		long diffCenterX = r1.getCenterX() - r2.getCenterX();
		long diffCenterY = r1.getCenterY() - r2.getCenterY();
		long radiiSum = r1.getRadius() + r2.getRadius();
		
		return diffCenterX*diffCenterX + diffCenterY*diffCenterY <= radiiSum*radiiSum;
	}

	private static boolean intersectsBoxCircle(Box r1, Circle r2) {
		// All computations are done with doubles to avoid rounding errors
		double centerX = r2.getCenterX() + 0.5;
		double centerY = r2.getCenterY() + 0.5;
		double diffCenterX = r1.getCenterX() - centerX;
		double diffCenterY = r1.getCenterY() - centerY;
		double width = r1.getWidth();
		double height = r1.getHeight();
		double distance = Math.sqrt(diffCenterX * diffCenterX + diffCenterY * diffCenterY);

		// Inner radius = shortest side / 2
		double innerRadius = Math.min(width, height) / 2;
		if (distance <= r2.getRadius() + innerRadius)
			return true;
		
		// Outer radius = diagonal / 2
		double outerRadius = Math.sqrt(width*width + height*height) / 2;
		if (distance >= r2.getRadius() + outerRadius)
			return false;

		double lengthFactor = r2.getRadius() / distance;

		double outerX = centerX + diffCenterX * lengthFactor;
		double outerY = centerY + diffCenterY * lengthFactor;

		return outerX >= r1.getXMin() && outerX < r1.getXMax() && outerY >= r1.getYMin() && outerY < r1.getYMax();
	}
}
