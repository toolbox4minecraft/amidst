package amidst.map.object;

import java.awt.Point;
import java.awt.image.BufferedImage;

import amidst.map.MapMarkers;
import amidst.map.layer.IconLayer;

public class MapObject extends Point {
	public MapMarkers type;
	public int rx;
	public int ry;
	public double localScale = 1.0;
	public IconLayer parentLayer;

	public MapObject(MapMarkers type, int x, int y) {
		super(x, y);
		this.type = type;
	}

	public void setParent(IconLayer parentLayer) {
		this.parentLayer = parentLayer;
	}

	public int getWidth() {
		return (int) (type.getImage().getWidth() * localScale);
	}

	public int getHeight() {
		return (int) (type.getImage().getHeight() * localScale);
	}

	public String getName() {
		return type.getName();
	}

	public BufferedImage getImage() {
		return type.getImage();
	}
}
