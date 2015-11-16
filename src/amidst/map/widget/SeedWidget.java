package amidst.map.widget;

import java.awt.Graphics2D;

import amidst.Options;
import amidst.map.MapViewer;

public class SeedWidget extends PanelWidget {
	public SeedWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(20, 30);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		setWidth(mapViewer.getFontMetrics().stringWidth(
				Options.instance.getSeedMessage()) + 20);
		super.draw(g2d, time);
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(Options.instance.getSeedMessage(), getX() + 10, getY() + 20);
	}
}
