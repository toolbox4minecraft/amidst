package amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.map.MapViewer;
import amidst.map.object.MapObject;

public class SelectedObjectWidget extends PanelWidget {
	private String message = "";
	private BufferedImage icon;

	public SelectedObjectWidget(MapViewer mapViewer) {
		super(mapViewer);
		increaseYPadding(40);
		setSize(20, 35);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		if (isTargetVisible()) {
			MapObject selectedObject = mapViewer.getSelectedObject();
			message = selectedObject.getName() + " [" + selectedObject.getRx()
					+ ", " + selectedObject.getRy() + "]" + " ["
					+ selectedObject.getWorldX() + ", "
					+ selectedObject.getWorldY() + "]";
			icon = selectedObject.getImage();
		}

		setWidth(45 + mapViewer.getFontMetrics().stringWidth(message));
		super.draw(g2d, time);

		g2d.setColor(TEXT_COLOR);
		double imgWidth = icon.getWidth();
		double imgHeight = icon.getHeight();
		double ratio = imgWidth / imgHeight;

		g2d.drawImage(icon, getX() + 5, getY() + 5, (int) (25. * ratio), 25,
				null);
		g2d.drawString(message, getX() + 35, getY() + 23);
	}

	@Override
	protected boolean onVisibilityCheck() {
		return (mapViewer.getSelectedObject() != null);
	}
}
