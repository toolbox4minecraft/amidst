package amidst.gui.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import amidst.gui.worldsurroundings.Zoom;
import amidst.preferences.PrefModel;

public class ScaleWidget extends Widget {
	@Deprecated
	public static int cScaleLengthMax_px = 200;
	@Deprecated
	public static int cMargin = 8;

	private final Zoom zoom;
	private final PrefModel<Boolean> isVisiblePreference;

	public ScaleWidget(CornerAnchorPoint anchor, Zoom zoom,
			PrefModel<Boolean> isVisiblePreference) {
		super(anchor);
		this.zoom = zoom;
		this.isVisiblePreference = isVisiblePreference;
		setWidth(100);
		setHeight(34);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, int viewerWidth, int viewerHeight,
			Point mousePosition, FontMetrics fontMetrics, float time) {
		int scaleBlocks = scaleLength_blocks();
		int scaleWidth_px = (int) (scaleBlocks * zoom.getCurrentValue());

		String message = scaleBlocks + " blocks";

		int stringWidth = fontMetrics.stringWidth(message);
		setWidth(Math.max(scaleWidth_px, stringWidth) + (cMargin * 2));
		drawBorderAndBackground(g2d, time, viewerWidth, viewerHeight);
		g2d.drawString(message, getX() + 1 + ((getWidth() - stringWidth) >> 1),
				getY() + 18);

		g2d.setColor(Color.white);

		g2d.setStroke(LINE_STROKE_2);
		g2d.drawLine(getX() + cMargin, getY() + 26, getX() + cMargin
				+ scaleWidth_px, getY() + 26);
		g2d.setStroke(LINE_STROKE_1);
		g2d.drawLine(getX() + cMargin, getY() + 23, getX() + cMargin,
				getY() + 28);
		g2d.drawLine(getX() + cMargin + scaleWidth_px, getY() + 23, getX()
				+ cMargin + scaleWidth_px, getY() + 28);
	}

	@Override
	protected boolean onVisibilityCheck() {
		return isVisiblePreference.get();
	}

	private int scaleLength_blocks() {
		double scale = zoom.getCurrentValue();

		int result = 1000;
		if (result * scale > cScaleLengthMax_px) {
			result = 500;
			if (result * scale > cScaleLengthMax_px) {
				result = 200;
				if (result * scale > cScaleLengthMax_px) {
					result = 100;
				}
			}
		}

		return result;
	}
}
