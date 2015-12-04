package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.map.MapViewer;
import amidst.preferences.PrefModel;
import amidst.utilities.FramerateTimer;

public class FpsWidget extends Widget {
	private final FramerateTimer fpsTimer;
	private final PrefModel<Boolean> isVisiblePreference;

	public FpsWidget(MapViewer mapViewer, CornerAnchorPoint anchor,
			FramerateTimer fpsTimer, PrefModel<Boolean> isVisiblePreference) {
		super(mapViewer, anchor);
		this.fpsTimer = fpsTimer;
		this.isVisiblePreference = isVisiblePreference;
		setWidth(20);
		setHeight(30);
		forceVisibility(onVisibilityCheck());
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics) {
		String framerate = fpsTimer.toString();
		fpsTimer.tick();
		setWidth(fontMetrics.stringWidth(framerate) + 20);
		drawBorderAndBackground(g2d, time);
		drawFramerate(g2d, framerate);
	}

	private void drawFramerate(Graphics2D g2d, String framerate) {
		g2d.drawString(framerate, getX() + 10, getY() + 20);
	}

	@Override
	protected boolean onVisibilityCheck() {
		return isVisiblePreference.get();
	}
}
