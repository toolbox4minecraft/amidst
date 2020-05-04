package amidst.gui.main.viewer.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Map.Entry;
import java.util.function.Supplier;

import amidst.documentation.ThreadSafe;

@ThreadSafe
public abstract class ProgressWidget extends OffsetWidget {
	private static final int VERTICAL_MARGIN = 4;
	private static final int HORIZONTAL_MARGIN = 4;
	
	private final Supplier<Entry<ProgressEntryType, Integer>> progressEntrySupplier;
	
	private int min;
	private int max;
	private int progress;

	public ProgressWidget(CornerAnchorPoint anchor, Supplier<Entry<ProgressEntryType, Integer>> progressEntrySupplier, int xOffset, int yOffset, int initialMin, int initialMax, int initialProgress, int width, int height) {
		super(anchor, xOffset, yOffset);
		this.progressEntrySupplier = progressEntrySupplier;
		setWidth(width);
		setHeight(height);
	}

	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		updateProgress(progressEntrySupplier.get());
	}
	
	private void updateProgress(Entry<ProgressEntryType, Integer> value) {
		if(value != null) {
			switch (value.getKey()) {
				case MIN:
					setMin(value.getValue());
					break;
				case MAX:
					setMax(value.getValue());
					break;
				case PROGRESS:
					setProgress(value.getValue());
					break;
			}
		}
	}

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

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	protected boolean onVisibilityCheck() {
		return progress < max && progress >= min;
	}
	
	public enum ProgressEntryType {
		MAX, MIN, PROGRESS
	}
	
}
