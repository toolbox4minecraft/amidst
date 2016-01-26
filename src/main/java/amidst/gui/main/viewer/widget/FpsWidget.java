package amidst.gui.main.viewer.widget;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.settings.Setting;

@NotThreadSafe
public class FpsWidget extends TextWidget {
	private final FramerateTimer fpsTimer;
	private final Setting<Boolean> isVisibleSetting;
	
	private static final int cAccelerationDotRadius = 4;

	@CalledOnlyBy(AmidstThread.EDT)
	public FpsWidget(CornerAnchorPoint anchor, FramerateTimer fpsTimer,
			Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.fpsTimer = fpsTimer;
		this.isVisibleSetting = isVisibleSetting;
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected String updateText() {
		fpsTimer.tick();
		if (isVisibleSetting.get()) {
			return "FPS: " + String.format("%.1f", fpsTimer.getCurrentFPS());
		} else {
			return null;
		}
	}	
	
	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		
		// Draw the frames per second text
		super.doDraw(g2d);
		
		// Draw a colored indicator dot to represent the gfx acceleration Ratio
		// * Green for fully accelerated 
		// * Blue for no acceleration
		// (avoiding red because it looks like a recording indicator, and 
		// also looks the same as green to red/green colorblind people) 
		float acceleratedComponent = getGraphicsAccelerationRatio();
		float unacceleratedComponent = 1.0f - acceleratedComponent;
		
		Color outlineColor = Color.lightGray;
		Color fillColor = new Color(
			0,                                             // Red
			Math.round(45 + 145 * acceleratedComponent),   // Green
			Math.round(255 * unacceleratedComponent)       // Blue
		);
		
		int dotX = getX() + super.getMarginLeft() + cAccelerationDotRadius;
		int dotY = getY() + (getHeight() >> 1);
		int diameter = cAccelerationDotRadius + cAccelerationDotRadius;
		
		Object originalAntialiasingHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(fillColor);
		g2d.fillOval(dotX - cAccelerationDotRadius + 1, dotY - cAccelerationDotRadius + 1, diameter - 1, diameter - 1);
		g2d.setColor(outlineColor);
		g2d.drawOval(dotX - cAccelerationDotRadius, dotY - cAccelerationDotRadius, diameter, diameter);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, originalAntialiasingHint);
	}
		
	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected int getMarginLeft() {
		return super.getMarginLeft() + cAccelerationDotRadius * 4;
	}	
}
