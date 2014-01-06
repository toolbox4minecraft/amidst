package amidst.map;
import amidst.Options;
import amidst.map.MapMarkers;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class MapObject extends Point {
	public MapMarkers type;
	public int rx, ry;
	public double localScale = 1.0;
	@Deprecated
	public double tempDist = 0;
	public IconLayer parentLayer;
	
	public MapObject(MapMarkers eType, int x, int y) {
		super(x, y);
		type = eType;
	}
	
	public String getName() {
		return type.toString();
	}
	
	public int getWidth() {
		return (int)(type.image.getWidth() * localScale);
	}
	public int getHeight() {
		return (int)(type.image.getHeight() * localScale);
	}
	
	public BufferedImage getImage() {
		return type.image;
	}
	
	public boolean isSelectable() {
		return Options.instance.showVillages.isSelected();
	}
	
	public MapObject setParent(IconLayer layer) {
		parentLayer = layer;
		return this;
	}
}
