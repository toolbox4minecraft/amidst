package amidst.map.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import amidst.resources.ResourceLoader;
import MoF.MapViewer;

public class PanelWidget extends Widget {
	private static BufferedImage
			dropShadowBottomLeft  = ResourceLoader.getImage("dropshadow/outer_bottom_left.png"),
			dropShadowBottomRight = ResourceLoader.getImage("dropshadow/outer_bottom_right.png"),
			dropShadowTopLeft     = ResourceLoader.getImage("dropshadow/outer_top_left.png"),
			dropShadowTopRight    = ResourceLoader.getImage("dropshadow/outer_top_right.png"),
			dropShadowBottom      = ResourceLoader.getImage("dropshadow/outer_bottom.png"),
			dropShadowTop         = ResourceLoader.getImage("dropshadow/outer_top.png"),
			dropShadowLeft        = ResourceLoader.getImage("dropshadow/outer_left.png"),
			dropShadowRight       = ResourceLoader.getImage("dropshadow/outer_right.png");
	public enum CornerAnchorPoint {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT,
		CENTER,
		NONE
	}
	protected Color textColor = new Color(1f, 1f, 1f);
	protected Color panelColor = new Color(0.15f, 0.15f, 0.15f, 0.8f);
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
		g2d.drawImage(dropShadowTopLeft,     x - 10,    y - 10,     null);
		g2d.drawImage(dropShadowTopRight,    x + width, y - 10,     null);
		g2d.drawImage(dropShadowBottomLeft,  x - 10,    y + height, null);
		g2d.drawImage(dropShadowBottomRight, x + width, y + height, null);
		
		g2d.drawImage(dropShadowTop,    x,         y - 10,     width, 10,  null);
		g2d.drawImage(dropShadowBottom, x,         y + height, width, 10,  null);
		g2d.drawImage(dropShadowLeft,   x - 10,    y,          10, height, null);
		g2d.drawImage(dropShadowRight,  x + width, y,          10, height, null);
		
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
		case CENTER:
			x = (mapViewer.getWidth() >> 1) - (width >> 1);
			y = (mapViewer.getHeight() >> 1) - (height >> 1);
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
