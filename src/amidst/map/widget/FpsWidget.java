package amidst.map.widget;

import java.awt.Graphics2D;

import amidst.Options;
import amidst.map.MapViewer;
import amidst.utilities.FramerateTimer;

public class FpsWidget extends Widget {
	private FramerateTimer fpsTimer = new FramerateTimer(2);

	public FpsWidget(MapViewer mapViewer, CornerAnchorPoint anchor) {
		super(mapViewer, anchor);
		setWidth(20);
		setHeight(30);
		forceVisibility(onVisibilityCheck());
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		String framerate = fpsTimer.toString();
		fpsTimer.tick();
		setWidth(mapViewer.getFontMetrics().stringWidth(framerate) + 20);
		super.draw(g2d, time);
		drawFramerate(g2d, framerate);
	}

	private void drawFramerate(Graphics2D g2d, String framerate) {
		g2d.setColor(TEXT_COLOR);
		g2d.drawString(framerate, getX() + 10, getY() + 20);
	}

	@Override
	protected boolean onVisibilityCheck() {
		return Options.instance.showFPS.get();
	}
}
