package amidst.map.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import amidst.map.MapViewer;
import amidst.resources.ResourceLoader;

public abstract class PanelWidget extends Widget {
	public static enum CornerAnchorPoint {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER, CENTER, NONE
	}

	private static final BufferedImage DROP_SHADOW_BOTTOM_LEFT = ResourceLoader
			.getImage("dropshadow/outer_bottom_left.png");
	private static final BufferedImage DROP_SHADOW_BOTTOM_RIGHT = ResourceLoader
			.getImage("dropshadow/outer_bottom_right.png");
	private static final BufferedImage DROP_SHADOW_TOP_LEFT = ResourceLoader
			.getImage("dropshadow/outer_top_left.png");
	private static final BufferedImage DROP_SHADOW_TOP_RIGHT = ResourceLoader
			.getImage("dropshadow/outer_top_right.png");
	private static final BufferedImage DROP_SHADOW_BOTTOM = ResourceLoader
			.getImage("dropshadow/outer_bottom.png");
	private static final BufferedImage DROP_SHADOW_TOP = ResourceLoader
			.getImage("dropshadow/outer_top.png");
	private static final BufferedImage DROP_SHADOW_LEFT = ResourceLoader
			.getImage("dropshadow/outer_left.png");
	private static final BufferedImage DROP_SHADOW_RIGHT = ResourceLoader
			.getImage("dropshadow/outer_right.png");

	private static final Color PANEL_COLOR = new Color(0.15f, 0.15f, 0.15f,
			0.8f);
	protected static final Color TEXT_COLOR = new Color(1f, 1f, 1f);
	protected static final Font TEXT_FONT = new Font("arial", Font.BOLD, 15);
	protected static final Stroke LINE_STROKE_1 = new BasicStroke(1);
	protected static final Stroke LINE_STROKE_2 = new BasicStroke(2,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	private CornerAnchorPoint anchor = CornerAnchorPoint.NONE;
	private int xPadding = 10;
	private int yPadding = 10;

	private float alpha = 1.0f;
	private float targetAlpha = 1.0f;
	private boolean isFading = false;
	private boolean isTargetVisible = true;

	protected PanelWidget(MapViewer mapViewer) {
		super(mapViewer);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		updateTargetAlpha();
		updateAlpha(time);
		updateIsFading();
		updatePosition();
		initGraphics(g2d);
		drawBorder(g2d);
		drawBackground(g2d);
	}

	private void updateTargetAlpha() {
		if (isTargetVisible) {
			targetAlpha = 1.0f;
		} else {
			targetAlpha = 0.0f;
		}
	}

	private void updateAlpha(float time) {
		if (alpha < targetAlpha) {
			alpha = Math.min(targetAlpha, alpha + time * 4.0f);
		} else if (alpha > targetAlpha) {
			alpha = Math.max(targetAlpha, alpha - time * 4.0f);
		}
	}

	private void updateIsFading() {
		isFading = alpha != targetAlpha;
	}

	private void updatePosition() {
		if (anchor == CornerAnchorPoint.TOP_LEFT) {
			x = xPadding;
			y = yPadding;
		} else if (anchor == CornerAnchorPoint.BOTTOM_LEFT) {
			x = xPadding;
			y = mapViewer.getHeight() - (height + yPadding);
		} else if (anchor == CornerAnchorPoint.BOTTOM_RIGHT) {
			x = mapViewer.getWidth() - (width + xPadding);
			y = mapViewer.getHeight() - (height + yPadding);
		} else if (anchor == CornerAnchorPoint.BOTTOM_CENTER) {
			x = (mapViewer.getWidth() >> 1) - (width >> 1);
			y = mapViewer.getHeight() - (height + yPadding);
		} else if (anchor == CornerAnchorPoint.TOP_RIGHT) {
			x = mapViewer.getWidth() - (width + xPadding);
			y = yPadding;
		} else if (anchor == CornerAnchorPoint.CENTER) {
			x = (mapViewer.getWidth() >> 1) - (width >> 1);
			y = (mapViewer.getHeight() >> 1) - (height >> 1);
		} else if (anchor == CornerAnchorPoint.NONE) {
			// TODO: set x and y
		}
	}

	private void initGraphics(Graphics2D g2d) {
		g2d.setColor(PANEL_COLOR);
	}

	private void drawBorder(Graphics2D g2d) {
		int x10 = x - 10;
		int y10 = y - 10;
		int xWidth = x + width;
		int yHeight = y + height;
		g2d.drawImage(DROP_SHADOW_TOP_LEFT, x10, y10, null);
		g2d.drawImage(DROP_SHADOW_TOP_RIGHT, xWidth, y10, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM_LEFT, x10, yHeight, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM_RIGHT, xWidth, yHeight, null);
		g2d.drawImage(DROP_SHADOW_TOP, x, y10, width, 10, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM, x, yHeight, width, 10, null);
		g2d.drawImage(DROP_SHADOW_LEFT, x10, y, 10, height, null);
		g2d.drawImage(DROP_SHADOW_RIGHT, xWidth, y, 10, height, null);
	}

	private void drawBackground(Graphics2D g2d) {
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

	@Override
	public boolean isVisible() {
		boolean value = (visible && isTargetVisible) || isFading;
		isTargetVisible = onVisibilityCheck();
		return value;
	}

	protected boolean onVisibilityCheck() {
		return visible;
	}

	public void forceVisibility(boolean value) {
		isTargetVisible = value;
		isFading = false;
		targetAlpha = value ? 1.0f : 0.0f;
		alpha = value ? 1.0f : 0.0f;
	}

	@Override
	public float getAlpha() {
		return alpha;
	}

	public PanelWidget setAnchorPoint(CornerAnchorPoint anchor) {
		this.anchor = anchor;
		return this;
	}

	public boolean isTargetVisible() {
		return isTargetVisible;
	}

	protected void increaseYPadding(int delta) {
		yPadding += delta;
	}
}
