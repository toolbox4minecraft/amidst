package amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.Point;

import MoF.MapViewer;

public class CursorInformationWidget extends PanelWidget {
	private String message = "";
	
	public CursorInformationWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(20, 30);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		Point mouseLocation = null;
		if ((mouseLocation = mapViewer.getMousePosition()) != null) {
			mouseLocation = map.screenToLocal(mouseLocation);
			String biomeName = map.getBiomeAliasAt(mouseLocation);
			message = biomeName + " [ " + mouseLocation.x + ", " + mouseLocation.y + " ]";
		}
		int stringWidth = mapViewer.getFontMetrics().stringWidth(message);
		setWidth(stringWidth + 20);
		super.draw(g2d, time);
		
		g2d.setColor(textColor);
		g2d.drawString(message, x + 10, y + 20);
	}
	
	@Override
	protected boolean onVisibilityCheck() {
		return (mapViewer.getMousePosition() != null);
	}
}
