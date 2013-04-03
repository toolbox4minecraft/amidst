package MoF;
import amidst.Options;
import amidst.map.MapMarkers;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class MapObject {
	public MapMarkers type;
	public int x, y, rx, ry;
	public double localScale = 1.0;
	public boolean selectable = true;
	public double tempDist = 0;
	protected JToggleButton.ToggleButtonModel model;
	public MapObject(MapMarkers eType, int eX, int eY) {
		type = eType;
		x = eX;
		y = eY;
		model = Options.instance.showIcons;
	}
	public String getName() {
		return type.toString();
	}
	public int getWidth() {
		if (model.isSelected())
			return (int)(type.image.getWidth()*localScale);
		return 0;
	}
	public int getHeight() {
		if (model.isSelected())
			return (int)(type.image.getHeight()*localScale);
		return 0;
	}
	
	public BufferedImage getImage() {
		return type.image;
	}
	
	public boolean isSelectable() {
		return Options.instance.showIcons.isSelected();
	}
}
