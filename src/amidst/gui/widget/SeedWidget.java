package amidst.gui.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class SeedWidget extends Widget {
	private final String text;

	public SeedWidget(CornerAnchorPoint anchor, long seed, String seedText) {
		super(anchor);
		this.text = createText(seed, seedText);
		setWidth(20);
		setHeight(30);
	}

	private String createText(long seed, String seedText) {
		if (seedText == null) {
			return "Seed: " + seed;
		} else {
			return "Seed: " + seedText + " (" + seed + ")";
		}
	}

	@Override
	public void draw(Graphics2D g2d, FontMetrics fontMetrics, float time) {
		setWidth(fontMetrics.stringWidth(text) + 20);
		drawBorderAndBackground(g2d, time);
		drawText(g2d, text);
	}

	private void drawText(Graphics2D g2d, String text) {
		g2d.drawString(text, getX() + 10, getY() + 20);
	}

	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
