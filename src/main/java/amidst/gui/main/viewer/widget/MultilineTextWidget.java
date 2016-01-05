package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class MultilineTextWidget extends Widget {
	private static final int LINE_HEIGHT = 20;
	private static final int PADDING = 10;

	private List<String> textLines;
	private boolean isVisible;
	private int ascent;

	@CalledOnlyBy(AmidstThread.EDT)
	protected MultilineTextWidget(CornerAnchorPoint anchor) {
		super(anchor);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		List<String> newTextLines = getTextLines();
		if (newTextLines != null && newTextLines != textLines) {
			textLines = newTextLines;
			ascent = fontMetrics.getAscent();
			setWidth(getNewWidth(fontMetrics));
			setHeight(getNewHeight());
		}
		isVisible = newTextLines != null;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getNewWidth(FontMetrics fontMetrics) {
		int result = 0;
		for (String line : textLines) {
			int textWidth = fontMetrics.stringWidth(line);
			if (result < textWidth) {
				result = textWidth;
			}
		}
		return result + 2 * PADDING;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getNewHeight() {
		return textLines.size() * LINE_HEIGHT + 2 * PADDING;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		for (int i = 0; i < textLines.size(); i++) {
			int x = getX() + PADDING;
			int y = getY() + PADDING + ascent + i * LINE_HEIGHT;
			g2d.drawString(textLines.get(i), x, y);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return isVisible;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract List<String> getTextLines();
}
