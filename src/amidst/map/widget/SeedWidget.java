package amidst.map.widget;

import java.awt.Graphics2D;

import MoF.MapViewer;
import amidst.Options;

public class SeedWidget extends PanelWidget {
	public SeedWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(20, 30);
	}
	
	@Override
	public void draw(Graphics2D g2d, float time) {
		setWidth(mapViewer.getFontMetrics().stringWidth(Options.instance.getSeedMessage()) + 20);
		super.draw(g2d, time);
		g2d.setColor(textColor);
		g2d.drawString(Options.instance.getSeedMessage(), x + 10, y + 20);
	}
}
