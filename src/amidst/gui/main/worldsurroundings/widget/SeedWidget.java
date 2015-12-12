package amidst.gui.main.worldsurroundings.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import amidst.mojangapi.world.WorldSeed;

public class SeedWidget extends Widget {
	private final String text;

	public SeedWidget(CornerAnchorPoint anchor, WorldSeed seed) {
		super(anchor);
		this.text = seed.getLabel();
		setWidth(20);
		setHeight(30);
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
