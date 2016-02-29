package amidst.gui.main.viewer.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class TextWidget extends Widget {
	private List<String> textLines = null;
	private boolean isVisible = false;

	@CalledOnlyBy(AmidstThread.EDT)
	protected TextWidget(CornerAnchorPoint anchor) {
		super(anchor);
		setWidth(20);
		setHeight(30);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {

		List<String> newTextLines = updateMultilineText();
		if (newTextLines != null && !newTextLines.equals(textLines)) {
			textLines = newTextLines;

			int maxStringWidth = 0;
			int stringHeight = 0;
			int lineHeight = fontMetrics.getHeight();
			for (String line : textLines) {

				if (stringHeight == lineHeight) {
					// A multiline string's height is 1 full LineHeight plus
					// (n - 1) line-separation-heights.
					lineHeight = getLineSeparationHeight(fontMetrics);
				}
				stringHeight += lineHeight;

				if (line != null) {
					maxStringWidth = Math.max(maxStringWidth,
							fontMetrics.stringWidth(line));
				}
			}

			setWidth(getMarginLeft() + maxStringWidth + getMarginRight());
			setHeight(Math.max(getMinimumHeight(), getMarginTop()
					+ stringHeight + getMarginBottom()));
		}
		isVisible = newTextLines != null && newTextLines.size() > 0;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		int x = getX() + getMarginLeft();
		int y = getY() + getMarginTop() + g2d.getFontMetrics().getAscent();

		int lineSeparationHeight = getLineSeparationHeight(g2d.getFontMetrics());
		for (String line : textLines) {
			if (line != null)
				g2d.drawString(line, x, y);
			y += lineSeparationHeight;
		}
	}

	@Override
	protected boolean onVisibilityCheck() {
		return isVisible;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	/** Override this to adjust the spacing between lines of text */
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
	/** 
	 * Widgit subclasses with multiple lines of text should override
	 * updateMultilineText(), rather than updateText()
	 */
	protected List<String> updateMultilineText() {
		return Arrays.asList(updateText());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	/** 
	 * Widgit subclasses with a single line of text should override 
	 * updateText() instead of updateMultilineText()
	 */
	protected String updateText() {
		// Execution should not reach here, as the subclass is supposed
		// to have overridden this method.
		// The only reason this isn't abstract is so that subclasses
		// which override updateMultilineText() don't have to provide
		// an implementation of updateText().
		throw new RuntimeException(
				"One of either updateMultilineText() or updateText() must be overridden");
	}
}
