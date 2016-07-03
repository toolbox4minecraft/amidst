package amidst.gui.main.viewer.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.gui.main.viewer.Zoom;
import amidst.settings.Setting;

@NotThreadSafe
public class ScaleWidget extends Widget {
	public static final int MAX_SCALE_LENGTH_ON_SCREEN = 200;
	public static final int MARGIN = 8;

	private final Zoom zoom;
	private final Setting<Boolean> isVisibleSetting;

	private int scaleLengthOnScreen;
	private String text;
	private int textWidth;

	@CalledOnlyBy(AmidstThread.EDT)
	public ScaleWidget(CornerAnchorPoint anchor, Zoom zoom, Setting<Boolean> isVisibleSetting) {
		super(anchor);
		this.zoom = zoom;
		this.isVisibleSetting = isVisibleSetting;
		setWidth(100);
		setHeight(34);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		int scaleLengthInWorld = getScaleLengthInWorld();
		scaleLengthOnScreen = (int) zoom.worldToScreen(scaleLengthInWorld);
		text = scaleLengthInWorld + " blocks";
		textWidth = fontMetrics.stringWidth(text);
		setWidth(Math.max(scaleLengthOnScreen, textWidth) + (MARGIN * 2));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getScaleLengthInWorld() {
		int first = 1;
		int base = 100;
		int result = first * base;
		int previousResult = result;
		while (zoom.worldToScreen(result) < MAX_SCALE_LENGTH_ON_SCREEN) {
			first = getNextFirst(first);
			base = getNextBase(first, base);
			previousResult = result;
			result = first * base;
		}
		return previousResult;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getNextFirst(int first) {
		if (first == 1) {
			return 2;
		} else if (first == 2) {
			return 5;
		} else {
			return 1;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getNextBase(int first, int base) {
		if (first == 1) {
			return base * 10;
		} else {
			return base;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		drawText(g2d);
		drawLines(g2d);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawText(Graphics2D g2d) {
		int x = getX() + 1 + ((getWidth() - textWidth) >> 1);
		int y = getY() + 18;
		g2d.drawString(text, x, y);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawLines(Graphics2D g2d) {
		int x1 = getX() + 1 + ((getWidth() - scaleLengthOnScreen) >> 1);
		int x2 = x1 + scaleLengthOnScreen;
		int y1 = getY() + 23;
		int y2 = getY() + 26;
		int y3 = getY() + 28;
		g2d.setColor(Color.white);
		g2d.setStroke(LINE_STROKE_2);
		g2d.drawLine(x1, y2, x2, y2);
		g2d.setStroke(LINE_STROKE_1);
		g2d.drawLine(x1, y1, x1, y3);
		g2d.drawLine(x2, y1, x2, y3);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return isVisibleSetting.get();
	}
}
