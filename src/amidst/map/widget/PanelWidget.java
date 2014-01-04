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
	
	public PanelWidget(MapViewer mapViewer) {
		super(mapViewer);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
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
	
	public PanelWidget setAnchorPoint(CornerAnchorPoint anchor) {
		this.anchor = anchor;
		return this;
	}
}
