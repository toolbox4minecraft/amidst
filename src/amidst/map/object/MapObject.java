package amidst.map.object;

import java.awt.Point;
import java.awt.image.BufferedImage;

import amidst.map.MapMarkers;
import amidst.map.layer.IconLayer;

public abstract class MapObject {
	public MapMarkers type;
	private int x;
	private int y;

	public int rx;
	public int ry;
	public double localScale = 1.0;
	public IconLayer parentLayer;

	public MapObject(MapMarkers type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public void setParent(IconLayer parentLayer) {
		this.parentLayer = parentLayer;
	}

	protected void setX(int x) {
		this.x = x;
	}

	protected void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return (int) (type.getImage().getWidth() * localScale);
	}

	public int getHeight() {
		return (int) (type.getImage().getHeight() * localScale);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getName() {
		return type.getName();
	}

	public BufferedImage getImage() {
		return type.getImage();
	}

	@Deprecated
	public Point getAsPoint() {
		return new Point(x, y);
	}
}
