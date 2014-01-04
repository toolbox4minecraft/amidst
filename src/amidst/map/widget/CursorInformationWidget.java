package amidst.map.widget;

import java.awt.Graphics2D;
import java.awt.Point;

import MoF.MapViewer;

public class CursorInformationWidget extends PanelWidget {
	public CursorInformationWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(20, 30);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		Point mouseLocation = map.screenToLocal(mapViewer.getMousePosition());
		String biomeName = map.getBiomeAliasAt(mouseLocation);
		String mouseLocationText = biomeName + " [ " + mouseLocation.x + ", " + mouseLocation.y + " ]";
		int stringWidth = mapViewer.getFontMetrics().stringWidth(mouseLocationText);
		setWidth(stringWidth + 20);
		super.draw(g2d, time);
		
		g2d.setColor(textColor);
		g2d.drawString(mouseLocationText, x + 10, y + 20);
	}
	
	@Override
	public boolean isVisible() {
		return visible && (mapViewer.getMousePosition() != null);
	}
}
