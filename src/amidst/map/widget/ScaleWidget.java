package amidst.map.widget;

import java.awt.Color;
import java.awt.Graphics2D;

import amidst.Options;
import amidst.map.MapViewer;

public class ScaleWidget extends PanelWidget {

	public static int cScaleLengthMax_px = 200;
	public static int cMargin = 8;

	public ScaleWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(100, 34);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {

		int scaleBlocks = scaleLength_blocks();
		int scaleWidth_px = (int) (scaleBlocks * map.getZoom());

		String message = scaleBlocks + " blocks";

		int stringWidth = mapViewer.getFontMetrics().stringWidth(message);
		setWidth(Math.max(scaleWidth_px, stringWidth) + (cMargin * 2));
		super.draw(g2d, time);

		g2d.setColor(TEXT_COLOR);
		g2d.setFont(TEXT_FONT);
		g2d.drawString(message, x + 1 + ((width - stringWidth) >> 1), y + 18);

		g2d.setColor(Color.white);

		g2d.setStroke(LINE_STROKE_2);
		g2d.drawLine(x + cMargin, y + 26, x + cMargin + scaleWidth_px, y + 26);
		g2d.setStroke(LINE_STROKE_1);
		g2d.drawLine(x + cMargin, y + 23, x + cMargin, y + 28);
		g2d.drawLine(x + cMargin + scaleWidth_px, y + 23, x + cMargin
				+ scaleWidth_px, y + 28);
	}

	@Override
	protected boolean onVisibilityCheck() {
		return Options.instance.showScale.get();
	}

	private int scaleLength_blocks() {

		double scale = map.getZoom();

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
