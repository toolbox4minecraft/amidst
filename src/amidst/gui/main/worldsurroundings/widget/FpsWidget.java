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

	private String framerate;

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
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		framerate = "FPS: " + String.format("%.1f", fpsTimer.getCurrentFPS());
		fpsTimer.tick();
		setWidth(fontMetrics.stringWidth(framerate) + 20);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		drawFramerate(g2d);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawFramerate(Graphics2D g2d) {
		g2d.drawString(framerate, getX() + 10, getY() + 20);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return isVisibleSetting.get();
	}
}
