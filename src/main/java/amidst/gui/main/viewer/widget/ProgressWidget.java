package amidst.gui.main.viewer.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public abstract class ProgressWidget extends OffsetWidget {
	private static final int VERTICAL_MARGIN = 4;
	private static final int HORIZONTAL_MARGIN = 4;
	private volatile int min;
	private volatile int max;
	private volatile int progress;

	public ProgressWidget(CornerAnchorPoint anchor, int xOffset, int yOffset, int initialMin, int initialMax, int initialProgress, int width, int height) {
		super(anchor, xOffset, yOffset);
		this.min = initialMin;
		this.max = initialMax;
		this.progress = initialProgress;
		setWidth(width);
		setHeight(height);
	}

	@Override
	protected abstract void doUpdate(FontMetrics fontMetrics, float time);

	@Override
	protected void doDraw(Graphics2D g2d) {
		int widthMax = getWidth() - (HORIZONTAL_MARGIN * 2) - 2;
		int calculatedWidth = (int) ((double) progress / max * widthMax);
		g2d.setColor(Color.GRAY);
		g2d.drawRect(
				getX() + HORIZONTAL_MARGIN,
				getY() + VERTICAL_MARGIN,
				getWidth() - (HORIZONTAL_MARGIN * 2) - 1,
				getHeight() - (VERTICAL_MARGIN * 2) -1
			);
		g2d.setColor(Color.GREEN.darker());
		g2d.fillRect(
				getX() + HORIZONTAL_MARGIN + 1,
				getY() + VERTICAL_MARGIN + 1,
				calculatedWidth,
				getHeight() - (VERTICAL_MARGIN * 2) - 2
			);
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	protected boolean onVisibilityCheck() {
		return (progress >= max || progress < min) ? false : true;
	}
	
}
