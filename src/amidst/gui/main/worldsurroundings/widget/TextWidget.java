package amidst.gui.main.worldsurroundings.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public abstract class TextWidget extends Widget {
	private String text = "";
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
		String newText = updateText();
		if (newText != null && !text.equals(newText)) {
			text = newText;
			setWidth(fontMetrics.stringWidth(text) + 20);
		}
		isVisible = newText != null;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		g2d.drawString(text, getX() + 10, getY() + 20);
	}

	@Override
	protected boolean onVisibilityCheck() {
		return isVisible;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	protected abstract String updateText();
}
