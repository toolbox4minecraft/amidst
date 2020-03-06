package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class TextWidget extends Widget {
	private List<String> textLines = null;
	private boolean isVisible = false;

	@CalledOnlyBy(AmidstThread.EDT)
	public TextWidget(CornerAnchorPoint anchor) {
		super(anchor);
		setWidth(20);
		setHeight(30);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		List<String> newTextLines = updateTextLines();
		if (newTextLines != null && !newTextLines.isEmpty() && !newTextLines.equals(textLines)) {
			textLines = newTextLines;
			setWidth(getMarginLeft() + getMaxStringWidth(fontMetrics) + getMarginRight());
			setHeight(Math.max(getMinimumHeight(), getMarginTop() + getStringHeight(fontMetrics) + getMarginBottom()));
		}
		isVisible = newTextLines != null && newTextLines.size() > 0;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getMaxStringWidth(FontMetrics fontMetrics) {
		int result = 0;
		for (String line : textLines) {
			result = Math.max(result, fontMetrics.stringWidth(line));
		}
		return result;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private int getStringHeight(FontMetrics fontMetrics) {
		int lineHeight = fontMetrics.getHeight();
		int lineSeparationHeight = getLineSeparationHeight(fontMetrics);
		return textLines.size() * lineSeparationHeight + lineHeight - lineSeparationHeight;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		int x = getX() + getMarginLeft();
		int y = getY() + getMarginTop() + g2d.getFontMetrics().getAscent();
		int lineSeparationHeight = getLineSeparationHeight(g2d.getFontMetrics());
		for (String line : textLines) {
			g2d.drawString(line, x, y);
			y += lineSeparationHeight;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected boolean onVisibilityCheck() {
		return isVisible;
	}

	/**
	 * Override this to adjust the spacing between lines of text
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	protected int getLineSeparationHeight(FontMetrics fontMetrics) {
		return fontMetrics.getHeight();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected int getMarginLeft() {
		return 10;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected int getMarginRight() {
		return 10;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected int getMarginTop() {
		return 6;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected int getMarginBottom() {
		return 6;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected int getMinimumHeight() {
		return 0;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract List<String> updateTextLines();
}
