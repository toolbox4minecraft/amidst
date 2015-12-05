package amidst.gui.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import amidst.minecraft.world.World;

public class SeedWidget extends Widget {
	private final World world;

	public SeedWidget(CornerAnchorPoint anchor, World world) {
		super(anchor);
		this.world = world;
		setWidth(20);
		setHeight(30);
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics,
			int viewerWidth, int viewerHeight, Point mousePosition) {
		String text = getText();
		setWidth(fontMetrics.stringWidth(text) + 20);
		drawBorderAndBackground(g2d, time, viewerWidth, viewerHeight);
		drawText(g2d, text);
	}

	private void drawText(Graphics2D g2d, String text) {
		g2d.drawString(text, getX() + 10, getY() + 20);
	}

	public String getText() {
		String seedText = world.getSeedText();
		if (seedText == null) {
			return "Seed: " + world.getSeed();
		} else {
			return "Seed: " + seedText + " (" + world.getSeed() + ")";
		}
	}

	@Override
	protected boolean onVisibilityCheck() {
		return true;
	}
}
