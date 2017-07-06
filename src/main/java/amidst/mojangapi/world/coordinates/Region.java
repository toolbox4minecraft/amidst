package amidst.mojangapi.world.coordinates;

import amidst.documentation.Immutable;

@Immutable
public abstract class Region {

	private Region() {}
	
	public abstract int getCenterX();
	public abstract int getCenterY();
	public Coordinates getCenter() {
		return Coordinates.from(getCenterX(), getCenterY());
	}
	public abstract Coordinates getCorner();
	public abstract int getWidth();
	public abstract int getHeight();
	
	public boolean isEmpty() {
		return getWidth() == 0 || getHeight() == 0;
	}
	
	public Coordinates getCornerNW() { return getCorner(); }
	public Coordinates getCornerNE() { return getCorner().add(getWidth(), 0); }
	public Coordinates getCornerSW() { return getCorner().add(0, getHeight()); }
	public Coordinates getCornerSE() { return getCorner().add(getWidth(), getHeight()); }
	
	public abstract Region getAs(Resolution resolution);
	public abstract Region.Box getEnclosing();
	
	public abstract boolean contains(int x, int y);
	public abstract boolean contains(Coordinates point);
	public abstract boolean contains(Region other);
	public abstract boolean intersectsWith(Region other);
	
	public abstract Region move(int dx, int dy);
	public abstract Region move(Coordinates offset);

	
	@Override
	public abstract boolean equals(Object other);
	
	@Override
	public abstract int hashCode();
	
	public static Box box(int x, int y, int w, int h) {
		if (w < 0 || h < 0)
			throw new IllegalArgumentException("width and height must be greater than or equals to 0");
		return new Box(x, y, x + w, y + h);
	}
	
	public static Box box(Coordinates corner, int w, int h) {
		return box(corner.getX(), corner.getY(), w, h);
	}
	
	public static Box box(Coordinates corner1, Coordinates corner2) {
		int xMin = Math.min(corner1.getX(), corner2.getX());
		int xMax = Math.max(corner1.getX(), corner2.getX());
		int yMin = Math.min(corner1.getY(), corner2.getY());
		int yMax = Math.max(corner1.getY(), corner2.getY());

		return new Box(xMin, yMin, xMax, yMax);
	}
	
	public static Box box(Coordinates center, int radius) {
		return box(center.substract(radius, radius), 2*radius+1, 2*radius+1);
	}
	
	public static Circle circle(Coordinates center, int radius) {
		return circle(center.getX(), center.getY(), radius);
	}
	
	public static Circle circle(int x, int y, int radius) {
		if(radius < 0)
			throw new IllegalArgumentException("radius must be greater than 0");
		return new Circle(x, y, radius);
	}
	
	public static final class Circle extends Region {
		private final int radius;
		private final long radiusSq;
		private final int x;
		private final int y;
		
		private Circle(int x, int y, int r) {
			radius = r;
			radiusSq = ((long) r) * r;
			this.x = x;
			this.y = y;
		}
		
		@Override
		public int getCenterX() {
			return x;
		}
		
		@Override
		public int getCenterY() {
			return y;
		}
		
		@Override
		public Coordinates getCorner() {
			return Coordinates.from(x - radius, y - radius);
		}
		
		@Override
		public int getWidth() {
			return radius*2;
		}
		@Override
		public int getHeight() {
			return radius*2;
		}
		
		public int getRadius() {
			return radius;
		}
		
		@Override
		public Region.Circle getAs(Resolution resolution) {
			return Region.circle(
					resolution.convertFromWorldToThis(x), 
					resolution.convertFromWorldToThis(y),
					resolution.convertFromWorldToThis(radius));
		}
		
		@Override
		public Region.Box getEnclosing() {
			return box(getCenter(), getRadius());
		}
		
		@Override
		public boolean contains(int x, int y) {
			return distanceSq(this.x, x, this.y, y) <= radiusSq;
		}
		@Override
		public boolean contains(Coordinates point) {
			return contains(point.getX(), point.getY());
		}
		
		@Override
		public boolean contains(Region other) {
			if(other instanceof Circle) {
				Circle o = (Circle) other;
				double dist = o.getCenter().getDistance(getCenter());
				return dist + o.radius <= radius;
			}

			return contains(other.getCornerNE().substract(1, 0))
					&& contains(other.getCornerNW())
					&& contains(other.getCornerSE().substract(1, 1))
					&& contains(other.getCornerNW().substract(0, 1));
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
		public Circle move(int dx, int dy) {
			return Region.circle(x+dx, y+dy, radius);
		}
		
		@Override
		public Circle move(Coordinates offset) {
			return move(offset.getX(), offset.getY());
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
			int result = x;
			result = 31*result + y;
			result = 31*result + radius;
			return result;
		}
		@Override
		public String toString() {
			return "[center=[" + x + ", " + y + "],radius=" + radius + "]";
		}
		
		
		private static long distanceSq(int x1, int x2, int y1, int y2) {
			long dx = x1-x2;
			long dy = y1-y2;
			return dx*dx+dy*dy;
		}
	}
	
	public static final class Box extends Region {
		private final int xMin;
		private final int yMin;
		private final int xMax;
		private final int yMax;

		private Box(int xMin, int yMin, int xMax, int yMax) {
			this.xMin = xMin;
			this.yMin = yMin;
			this.xMax = xMax;
			this.yMax = yMax;
		}
		
		@Override
		public int getCenterX() {
			return (xMin+xMax)/2;
		}
		
		@Override
		public int getCenterY() {
			return (yMin+yMax)/2;
		}

		@Override
		public int getWidth() {
			return xMax - xMin;
		}

		@Override
		public int getHeight() {
			return yMax - yMin;
		}
		
		@Override
		public Region.Box getAs(Resolution resolution) {
			return Region.box(Coordinates.from(xMin, yMin).getAs(resolution),
					Coordinates.from(xMax, yMax).getAs(resolution));
		}
		
		public Region.Box getEnclosing() {
			return this;
		}

		@Override
		public boolean contains(int posX, int posY) {
			return posX >= getXMin() && posX < getXMax() && posY >= getYMin() && posY < getYMax();
		}

		@Override
		public boolean contains(Coordinates pos) {
			return contains(pos.getX(), pos.getY());
		}
		
		@Override
		public boolean contains(Region other) {
			return contains(other.getCornerNW()) && contains(other.getCornerSE().substract(1, 1));
		}

		@Override
		public boolean intersectsWith(Region other) {
			if(other instanceof Box)
				return intersectsBoxBox(this, (Box) other);
			if(other instanceof Circle)
				return intersectsBoxCircle(this, (Circle) other);
			
			throw new IllegalArgumentException("unknown region type " + other.getClass().getCanonicalName());
		}

		@Override
		public Box move(int dx, int dy) {
			return new Box(xMin+dx, yMin+dy, xMax+dx, yMax+dy);
		}
		
		@Override
		public Box move(Coordinates offset) {
			return move(offset.getX(), offset.getY());
		}

		public int getXMin() {
			return xMin;
		}

		public int getYMin() {
			return yMin;
		}

		public int getXMax() {
			return xMax;
		}

		public int getYMax() {
			return yMax;
		}
		
		@Override
		public Coordinates getCorner() {
			return Coordinates.from(xMin, yMin);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 13;
			result = prime * result + xMin;
			result = prime * result + yMin;
			result = prime * result + xMax;
			result = prime * result + yMax;
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
