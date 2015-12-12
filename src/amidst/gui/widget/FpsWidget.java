package amidst.gui.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.settings.Setting;

public class FpsWidget extends Widget {
	private final FramerateTimer fpsTimer;
	private final Setting<Boolean> isVisibleSetting;

	public FpsWidget(CornerAnchorPoint anchor, FramerateTimer fpsTimer,
			Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.fpsTimer = fpsTimer;
		this.isVisibleSetting = isVisibleSetting;
		setWidth(20);
		setHeight(30);
		forceVisibility(onVisibilityCheck());
	}

	@Override
	public void draw(Graphics2D g2d, FontMetrics fontMetrics, float time) {
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
		return isVisibleSetting.get();
	}
}
