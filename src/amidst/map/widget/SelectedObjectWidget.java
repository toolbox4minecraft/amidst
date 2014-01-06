package amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.map.MapObject;
import MoF.MapViewer;

public class SelectedObjectWidget extends PanelWidget {
	private String message = "";
	private BufferedImage icon;
	
	public SelectedObjectWidget(MapViewer mapViewer) {
		super(mapViewer);
		yPadding += 40;
		setDimensions(20, 35);
		forceVisibility(false);
	}
	
	@Override
	public void draw(Graphics2D g2d, float time) {
		if (targetVisibility) {
			MapObject selectedObject = mapViewer.getSelectedObject();
			message = selectedObject.getName() + " [" + selectedObject.rx + ", " + selectedObject.ry + "]";
			icon = selectedObject.getImage();
		}

		setWidth(45 + mapViewer.getFontMetrics().stringWidth(message));
		super.draw(g2d, time);

		g2d.setColor(textColor);
		double imgWidth = icon.getWidth();
		double imgHeight = icon.getHeight();
		double ratio = imgWidth/imgHeight;
		
		g2d.drawImage(icon, x + 5, y + 5, (int)(25.*ratio), 25, null);
		g2d.drawString(message, x + 35, y + 23);
	}
	
	@Override
	protected boolean onVisibilityCheck() {
		return (mapViewer.getSelectedObject() != null);
	}
}
