package amidst.map.widget;

import java.awt.Graphics2D;

import MoF.MapViewer;
import amidst.Options;
import amidst.utilties.FramerateTimer;

public class FpsWidget extends PanelWidget {
	private FramerateTimer fps = new FramerateTimer(2);
	public FpsWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(20, 30);
		forceVisibility(onVisibilityCheck());
	}
	
	@Override 
	public void draw(Graphics2D g2d, float time) {
		String framerate = fps.toString();
		setWidth(mapViewer.getFontMetrics().stringWidth(framerate) + 20);
		super.draw(g2d, time);
		
		fps.tick();
		g2d.setColor(textColor);
		g2d.drawString(framerate, x + 10, y + 20);
	}
	
	@Override
	protected boolean onVisibilityCheck() {
		return Options.instance.showFPS.get();
	}
}
