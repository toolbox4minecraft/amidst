package amidst.gui.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import amidst.resources.ResourceLoader;
import amidst.utilities.CoordinateUtils;

public abstract class Widget {
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

	public static final Font TEXT_FONT = new Font("arial", Font.BOLD, 15);
	private static final Color TEXT_COLOR = new Color(1f, 1f, 1f);
	private static final Color PANEL_COLOR = new Color(0.15f, 0.15f, 0.15f,
			0.8f);

	protected static final Stroke LINE_STROKE_1 = new BasicStroke(1);
	protected static final Stroke LINE_STROKE_2 = new BasicStroke(2,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	private final CornerAnchorPoint anchor;

	private int x;
	private int y;
	private int width;
	private int height;

	private int xMargin = 10;
	private int yMargin = 10;

	private float alpha = 1.0f;
	private float targetAlpha = 1.0f;

	protected Widget(CornerAnchorPoint anchor) {
		this.anchor = anchor;
	}

	public void drawBorderAndBackground(Graphics2D g2d, float time,
			int viewerWidth, int viewerHeight) {
		updateAlpha(time);
		updatePosition(viewerWidth, viewerHeight);
		initGraphics(g2d);
		drawBorder(g2d);
		drawBackground(g2d);
		initGraphicsForContent(g2d);
	}

	private void updateAlpha(float time) {
		if (alpha < targetAlpha) {
			alpha = Math.min(targetAlpha, alpha + time * 4.0f);
		} else if (alpha > targetAlpha) {
			alpha = Math.max(targetAlpha, alpha - time * 4.0f);
		}
	}

	private void updatePosition(int viewerWidth, int viewerHeight) {
		if (anchor == CornerAnchorPoint.TOP_LEFT) {
			setX(getLeftX());
			setY(getTopY());
		} else if (anchor == CornerAnchorPoint.BOTTOM_LEFT) {
			setX(getLeftX());
			setY(getBottomY(viewerHeight));
		} else if (anchor == CornerAnchorPoint.BOTTOM_RIGHT) {
			setX(getRightX(viewerWidth));
			setY(getBottomY(viewerHeight));
		} else if (anchor == CornerAnchorPoint.BOTTOM_CENTER) {
			setX(getCenterX(viewerWidth));
			setY(getBottomY(viewerHeight));
		} else if (anchor == CornerAnchorPoint.TOP_RIGHT) {
			setX(getRightX(viewerWidth));
			setY(getTopY());
		} else if (anchor == CornerAnchorPoint.CENTER) {
			setX(getCenterX(viewerWidth));
			setY(getCenterY(viewerHeight));
		} else if (anchor == CornerAnchorPoint.NONE) {
			// TODO: set x and y?
		}
	}

	private int getLeftX() {
		return xMargin;
	}

	private int getTopY() {
		return yMargin;
	}

	private int getRightX(int viewerWidth) {
		return viewerWidth - (getWidth() + xMargin);
	}

	private int getBottomY(int viewerHeight) {
		return viewerHeight - (getHeight() + yMargin);
	}

	private int getCenterX(int viewerWidth) {
		return (viewerWidth >> 1) - (getWidth() >> 1);
	}

	private int getCenterY(int viewerHeight) {
		return (viewerHeight >> 1) - (getHeight() >> 1);
	}

	private void initGraphics(Graphics2D g2d) {
		g2d.setColor(PANEL_COLOR);
	}

	private void drawBorder(Graphics2D g2d) {
		int x10 = getX() - 10;
		int y10 = getY() - 10;
		int xWidth = getX() + getWidth();
		int yHeight = getY() + getHeight();
		g2d.drawImage(DROP_SHADOW_TOP_LEFT, x10, y10, null);
		g2d.drawImage(DROP_SHADOW_TOP_RIGHT, xWidth, y10, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM_LEFT, x10, yHeight, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM_RIGHT, xWidth, yHeight, null);
		g2d.drawImage(DROP_SHADOW_TOP, getX(), y10, getWidth(), 10, null);
		g2d.drawImage(DROP_SHADOW_BOTTOM, getX(), yHeight, getWidth(), 10, null);
		g2d.drawImage(DROP_SHADOW_LEFT, x10, getY(), 10, getHeight(), null);
		g2d.drawImage(DROP_SHADOW_RIGHT, xWidth, getY(), 10, getHeight(), null);
	}

	private void drawBackground(Graphics2D g2d) {
		g2d.fillRect(getX(), getY(), getWidth(), getHeight());
	}

	private void initGraphicsForContent(Graphics2D g2d) {
		g2d.setFont(TEXT_FONT);
		g2d.setColor(TEXT_COLOR);
	}

	private float getAlpha(boolean isVisible) {
		if (isVisible) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

	private boolean isFading() {
		return targetAlpha != alpha;
	}

	protected void increaseYMargin(int delta) {
		yMargin += delta;
	}

	public boolean isInBounds(Point mouse) {
		return isInBounds(mouse.x, mouse.y);
	}

	public boolean isInBounds(int x, int y) {
		return CoordinateUtils.isInBounds(x, y, this.x, this.y, this.width,
				this.height);
	}

	public int translateXToWidgetCoordinates(Point mouse) {
		return translateXToWidgetCoordinates(mouse.x);
	}

	public int translateYToWidgetCoordinates(Point mouse) {
		return translateYToWidgetCoordinates(mouse.y);
	}

	public int translateXToWidgetCoordinates(int x) {
		return x - this.x;
	}

	public int translateYToWidgetCoordinates(int y) {
		return y - this.y;
	}

	public int translateXToScreenCoordinates(int x) {
		return this.x + x;
	}

	public int translateYToScreenCoordinates(int y) {
		return this.y + y;
	}

	protected void forceVisibility(boolean isVisible) {
		targetAlpha = getAlpha(isVisible);
		alpha = targetAlpha;
	}

	public boolean isVisible() {
		boolean isVisible = onVisibilityCheck();
		targetAlpha = getAlpha(isVisible);
		return isVisible || isFading();
	}

	public float getAlpha() {
		return alpha;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	protected void setX(int x) {
		this.x = x;
	}

	protected void setY(int y) {
		this.y = y;
	}

	protected void setWidth(int width) {
		this.width = width;
	}

	protected void setHeight(int height) {
		this.height = height;
	}

	public boolean onClick(int x, int y) {
		return true;
	}

	public boolean onMouseWheelMoved(int x, int y, int rotation) {
		return false;
	}

	public boolean onMousePressed(int x, int y) {
		return true;
	}

	public void onMouseReleased() {
	}

	public abstract void draw(Graphics2D g2d, float time,
			FontMetrics fontMetrics, int viewerWidth, int viewerHeight,
			Point mousePosition);

	protected abstract boolean onVisibilityCheck();
}
