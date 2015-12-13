package amidst.gui.main.worldsurroundings.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;

@NotThreadSafe
public class FpsWidget extends Widget {
	private final FramerateTimer fpsTimer;
	private final Setting<Boolean> isVisibleSetting;

	@CalledOnlyBy(AmidstThread.EDT)
	public FpsWidget(CornerAnchorPoint anchor, FramerateTimer fpsTimer,
			Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.fpsTimer = fpsTimer;
		this.isVisibleSetting = isVisibleSetting;
		setWidth(20);
		setHeight(30);
		forceVisibility(onVisibilityCheck());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void draw(Graphics2D g2d, FontMetrics fontMetrics, float time) {
		String framerate = "FPS: "
				+ String.format("%.1f", fpsTimer.getCurrentFPS());
		fpsTimer.tick();
		setWidth(fontMetrics.stringWidth(framerate) + 20);
		drawBorderAndBackground(g2d, time);
		drawFramerate(g2d, framerate);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawFramerate(Graphics2D g2d, String framerate) {
		g2d.drawString(framerate, getX() + 10, getY() + 20);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return isVisibleSetting.get();
	}
}
