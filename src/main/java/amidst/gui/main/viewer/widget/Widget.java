package amidst.gui.main.viewer.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import amidst.ResourceLoader;
import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class Widget {
	public static enum CornerAnchorPoint {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT,
		BOTTOM_CENTER,
		CENTER,
		NONE
	}

	protected static boolean isInBounds(int x, int y, int offsetX, int offsetY, int width, int height) {
		return x >= offsetX && x < offsetX + width && y >= offsetY && y < offsetY + height;
	}

	private static final BufferedImage DROP_SHADOW_BOTTOM_LEFT = ResourceLoader
			.getImage("/amidst/gui/main/dropshadow/outer_bottom_left.png");
	private static final BufferedImage DROP_SHADOW_BOTTOM_RIGHT = ResourceLoader
			.getImage("/amidst/gui/main/dropshadow/outer_bottom_right.png");
	private static final BufferedImage DROP_SHADOW_TOP_LEFT = ResourceLoader
			.getImage("/amidst/gui/main/dropshadow/outer_top_left.png");
	private static final BufferedImage DROP_SHADOW_TOP_RIGHT = ResourceLoader
			.getImage("/amidst/gui/main/dropshadow/outer_top_right.png");
	private static final BufferedImage DROP_SHADOW_BOTTOM = ResourceLoader
			.getImage("/amidst/gui/main/dropshadow/outer_bottom.png");
	private static final BufferedImage DROP_SHADOW_TOP = ResourceLoader
			.getImage("/amidst/gui/main/dropshadow/outer_top.png");
	private static final BufferedImage DROP_SHADOW_LEFT = ResourceLoader
			.getImage("/amidst/gui/main/dropshadow/outer_left.png");
	private static final BufferedImage DROP_SHADOW_RIGHT = ResourceLoader
			.getImage("/amidst/gui/main/dropshadow/outer_right.png");

	public static final Font TEXT_FONT = new Font("arial", Font.BOLD, 15);
	private static final Color TEXT_COLOR = new Color(1f, 1f, 1f);
	private static final Color PANEL_COLOR = new Color(0.15f, 0.15f, 0.15f, 0.8f);

	protected static final Stroke LINE_STROKE_1 = new BasicStroke(1);
	protected static final Stroke LINE_STROKE_2 = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	private final CornerAnchorPoint anchor;

	private int x;
	private int y;
	private int width;
	private int height;

	private int xMargin = 10;
	private int yMargin = 10;

	private boolean isFirstVisibilityCheck = true;
	private float alpha = 1.0f;
	private float targetAlpha = 1.0f;

	private int viewerWidth;
	private int viewerHeight;
	private Point mousePosition;

	@CalledOnlyBy(AmidstThread.EDT)
	protected Widget(CornerAnchorPoint anchor) {
		this.anchor = anchor;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void update(int viewerWidth, int viewerHeight, Point mousePosition, FontMetrics fontMetrics, float time) {
		this.viewerWidth = viewerWidth;
		this.viewerHeight = viewerHeight;
		this.mousePosition = mousePosition;
		updateAlpha(time);
		doUpdate(fontMetrics, time);
		updatePosition();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateAlpha(float time) {
		if (alpha < targetAlpha) {
			alpha = Math.min(targetAlpha, alpha + time * 4.0f);
		} else if (alpha > targetAlpha) {
			alpha = Math.max(targetAlpha, alpha - time * 4.0f);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updatePosition() {
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

	@CalledOnlyBy(AmidstThread.EDT)
	private int getLeftX() {
		return xMargin;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getTopY() {
		return yMargin;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getRightX(int viewerWidth) {
		return viewerWidth - (width + xMargin);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getBottomY(int viewerHeight) {
		return viewerHeight - (height + yMargin);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getCenterX(int viewerWidth) {
		return (viewerWidth >> 1) - (width >> 1);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getCenterY(int viewerHeight) {
		return (viewerHeight >> 1) - (height >> 1);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void draw(Graphics2D g2d) {
		initGraphics(g2d);
		drawBorder(g2d);
		drawBackground(g2d);
		initGraphicsForContent(g2d);
		doDraw(g2d);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void initGraphics(Graphics2D g2d) {
		g2d.setColor(PANEL_COLOR);
	}

	@CalledOnlyBy(AmidstThread.EDT)
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

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawBackground(Graphics2D g2d) {
		g2d.fillRect(x, y, width, height);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void initGraphicsForContent(Graphics2D g2d) {
		g2d.setFont(TEXT_FONT);
		g2d.setColor(TEXT_COLOR);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void increaseYMargin(int delta) {
		yMargin += delta;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isInBounds(Point mouse) {
		return isInBounds(mouse.x, mouse.y);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isInBounds(int x, int y) {
		return isInBounds(x, y, this.x, this.y, this.width, this.height);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int translateXToWidgetCoordinates(Point mouse) {
		return translateXToWidgetCoordinates(mouse.x);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int translateYToWidgetCoordinates(Point mouse) {
		return translateYToWidgetCoordinates(mouse.y);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int translateXToWidgetCoordinates(int x) {
		return x - this.x;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int translateYToWidgetCoordinates(int y) {
		return y - this.y;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean isVisible() {
		boolean isVisible = onVisibilityCheck();
		targetAlpha = getTargetAlpha(isVisible);
		if (isFirstVisibilityCheck) {
			isFirstVisibilityCheck = false;
			skipFading();
		}
		return isVisible || isFading();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private float getTargetAlpha(boolean isVisible) {
		if (isVisible) {
			return 1.0f;
		} else {
			return 0.0f;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void skipFading() {
		alpha = targetAlpha;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isFading() {
		return targetAlpha != alpha;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public float getAlpha() {
		return alpha;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getX() {
		return x;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getY() {
		return y;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getWidth() {
		return width;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public int getHeight() {
		return height;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void setX(int x) {
		this.x = x;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void setY(int y) {
		this.y = y;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void setWidth(int width) {
		this.width = width;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected void setHeight(int height) {
		this.height = height;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean onClick(int x, int y) {
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean onMouseWheelMoved(int x, int y, int rotation) {
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean onMousePressed(int x, int y) {
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void onMouseReleased() {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected int getViewerWidth() {
		return viewerWidth;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected int getViewerHeight() {
		return viewerHeight;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected Point getMousePosition() {
		return mousePosition;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract void doUpdate(FontMetrics fontMetrics, float time);

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract void doDraw(Graphics2D g2d);

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract boolean onVisibilityCheck();
}
