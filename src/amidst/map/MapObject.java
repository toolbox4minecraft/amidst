package amidst.map;
import amidst.Options;
import amidst.map.MapMarkers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MapObject extends Point {
	public MapMarkers type;
	public int rx, ry;
	public double localScale = 1.0;
	@Deprecated
	public double tempDist = 0;
	protected JToggleButton.ToggleButtonModel model;
	public IconLayer parentLayer;
	
	public MapObject(MapMarkers eType, int x, int y) {
		super(x, y);
		type = eType;
		model = Options.instance.showIcons;
	}
	
	public String getName() {
		return type.toString();
	}
	
	
	public int getWidth() {
		if (model.isSelected())
			return (int)(type.image.getWidth() * localScale);
		return 0;
	}
	public int getHeight() {
		if (model.isSelected())
			return (int)(type.image.getHeight() * localScale);
		return 0;
	}
	
	public BufferedImage getImage() {
		return type.image;
	}
	
	public boolean isSelectable() {
		return Options.instance.showIcons.isSelected();
	}
	
	public MapObject setParent(IconLayer layer) {
		parentLayer = layer;
		return this;
	}
}
