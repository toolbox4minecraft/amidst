package amidst.map.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import MoF.MapViewer;

public class PanelWidget extends Widget {
	public enum CornerAnchorPoint {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT,
		NONE
	}
	protected Color textColor = new Color(1f, 1f, 1f);
	protected Color panelColor = new Color(0.2f, 0.2f, 0.2f, 0.8f);
	protected Font textFont = new Font("arial", Font.BOLD, 15);
	protected CornerAnchorPoint anchor = CornerAnchorPoint.NONE;
	protected int xPadding = 10, yPadding = 10;
	
	protected float alpha = 1.0f, targetAlpha = 1.0f;
	protected boolean isFading = false;
	protected boolean targetVisibility = true;
	
	public PanelWidget(MapViewer mapViewer) {
		super(mapViewer);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		targetAlpha = targetVisibility?1.0f:0.0f;
		if (alpha < targetAlpha)
			alpha = Math.min(targetAlpha, alpha + time*4.0f);
		else if (alpha > targetAlpha)
			alpha = Math.max(targetAlpha, alpha - time*4.0f);
		isFading = (alpha != targetAlpha);
		
		updatePosition();
		g2d.setColor(panelColor);
		g2d.fillRect(x, y, width, height);
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	protected void updatePosition() {
		switch (anchor) {
		case TOP_LEFT:
			x = xPadding;
			y = yPadding;
			break;
		case BOTTOM_LEFT:
			x = xPadding;
			y = mapViewer.getHeight() - (height + yPadding);
			break;
		case BOTTOM_RIGHT:
			x = mapViewer.getWidth()  - (width  + xPadding);
			y = mapViewer.getHeight() - (height + yPadding);
			break;
		case TOP_RIGHT:
			x = mapViewer.getWidth()  - (width  + xPadding);
			y = yPadding;
			break;
		case NONE:
			break;
		}
	}
	
	@Override
	public boolean isVisible() {
		boolean value = (visible && targetVisibility) || isFading;
		targetVisibility = onVisibilityCheck();
		return value;
	}
	
	protected boolean onVisibilityCheck() {
		return visible;
	}
	
	public void forceVisibility(boolean value) {
		targetVisibility = value;
		isFading = false;
		targetAlpha = value?1.0f:0.0f;
		alpha = value?1.0f:0.0f;
	}
	
	@Override
	public float getAlpha() {
		return alpha;
	}
	
	public PanelWidget setAnchorPoint(CornerAnchorPoint anchor) {
		this.anchor = anchor;
		return this;
	}
}
