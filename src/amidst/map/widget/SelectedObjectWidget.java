package amidst.map.widget;

import java.awt.Graphics2D;

import amidst.map.MapObject;
import MoF.MapViewer;

public class SelectedObjectWidget extends PanelWidget {
	public SelectedObjectWidget(MapViewer mapViewer) {
		super(mapViewer);
		yPadding += 40;
		setDimensions(20, 35);
	}
	
	@Override
	public void draw(Graphics2D g2d, float time) {
		MapObject selectedObject = mapViewer.getSelectedObject();
		String selectionMessage = selectedObject.getName() + " [" + selectedObject.rx + ", " + selectedObject.ry + "]";
		setWidth(45 + mapViewer.getFontMetrics().stringWidth(selectionMessage));
		super.draw(g2d, time);

		g2d.setColor(textColor);
		double imgWidth = selectedObject.getWidth();
		double imgHeight = selectedObject.getHeight();
		double ratio = imgWidth/imgHeight;
		
		g2d.drawImage(selectedObject.getImage(), x + 5, y + 5, (int)(25.*ratio), 25, null);
		g2d.drawString(selectionMessage, x + 35, y + 23);
	}

	
	@Override
	public boolean isVisible() {
		return visible && (mapViewer.getSelectedObject() != null);
	}
}
